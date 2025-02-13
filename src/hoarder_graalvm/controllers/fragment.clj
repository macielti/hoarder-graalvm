(ns hoarder-graalvm.controllers.fragment
  (:require [clojure.java.io :as io]
            [hoarder-graalvm.cryptograph :as cryptograph]
            [hoarder-graalvm.diplomat.db.postgresql.fragment :as database.fragment]
            [hoarder-graalvm.diplomat.http-client :as diplomat.http-client]
            [hoarder-graalvm.diplomat.telegram.producer :as diplomat.telegram.producer]
            [hoarder-graalvm.logic.file :as logic.file]
            [hoarder-graalvm.logic.fragment :as logic.fragment]
            [hoarder-graalvm.models.fragment :as models.fragment]
            [schema.core :as s])
  (:import (java.io File)
           (org.pg Pool)))

(s/defn process-fragments-upload! :- [models.fragment/Fragment]
  [file-id :- s/Uuid
   file-fragments :- [File]
   telegram-chat-id :- s/Str
   telegram-token :- s/Str
   http-client]
  (let [fragment-indexes (-> (count file-fragments) range)]
    (doall (pmap
            (fn [file fragment-index]
              (-> (diplomat.telegram.producer/send-document! file telegram-chat-id telegram-token http-client)
                  (logic.fragment/->fragment file-id fragment-index file)))
            file-fragments fragment-indexes))))

(s/defn by-file :- [models.fragment/Fragment]
  [file-id :- s/Uuid
   postgresql :- Pool]
  (database.fragment/by-file-id file-id postgresql))

(s/defn ^:private download-fragment!
  [fragment :- models.fragment/Fragment
   telegram-token :- s/Str
   http-client]
  (let [encrypted-file-fragment (io/file (logic.fragment/output-encrypted-fragment-file-path fragment))
        file-fragment (io/file (logic.fragment/output-encrypted-fragment-file-path fragment))]
    (-> (diplomat.http-client/fetch-telegram-file-path (:fragment/external-file-id fragment) telegram-token http-client)
        (diplomat.http-client/download-telegram-document! telegram-token http-client)
        (io/copy encrypted-file-fragment))
    (cryptograph/decrypt-file! encrypted-file-fragment file-fragment (str (:fragment/file-id fragment)))
    file-fragment))

(s/defn ^:private download-fragments! :- [File]
  [fragments :- [models.fragment/Fragment]
   telegram-token :- s/Str
   http-client]
  (-> (pmap #(download-fragment! % telegram-token http-client) fragments)
      doall))

(s/defn merge-files! :- File
  "Merge multiple files into a single file."
  [output-file :- File
   input-files :- File]
  (with-open [writer (io/output-stream output-file)]
    (doseq [file input-files]
      (with-open [reader (io/input-stream file)]
        (io/copy reader writer))))
  output-file)

(s/defn fragments->file! :- File
  [file-id :- s/Uuid
   telegram-token :- s/Str
   pool :- Pool
   http-client]
  (let [fragments (->> (database.fragment/by-file-id file-id pool)
                       (sort-by :fragment/index))
        downloaded-file-fragments (download-fragments! fragments telegram-token http-client)
        downloaded-file (-> (logic.file/joined-fragments-file-path file-id)
                            io/file)]
    (merge-files! downloaded-file downloaded-file-fragments)
    (io/file (logic.file/joined-fragments-file-path file-id))))
