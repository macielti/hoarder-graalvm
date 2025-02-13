(ns hoarder-graalvm.controllers.file
  (:require [buddy.core.codecs :as buddy-codecs]
            [buddy.core.hash :as hash]
            [clojure.java.io :as io]
            [clojure.string]
            [hoarder-graalvm.controllers.fragment :as controllers.fragment]
            [hoarder-graalvm.cryptograph :as cryptograph]
            [hoarder-graalvm.diplomat.db.postgresql.file :as database.file]
            [hoarder-graalvm.diplomat.http-client :as diplomat.http-client]
            [hoarder-graalvm.models.file :as models.file]
            [schema.core :as s])
  (:import (java.io File)))

(s/defn create-file! :- models.file/File
  [file :- models.file/File
   pool]
  (database.file/insert! file pool))

(s/defn fetch-file :- (s/maybe models.file/File)
  [file-id :- s/Uuid
   pool]
  (database.file/lookup file-id pool))

(def CHUNK_SIZE_BYTES 20971520)

(s/defn ^:private split-file! :- [File]
  [absolute-file-path :- s/Str
   file-id :- s/Uuid]
  (with-open [reader (io/input-stream absolute-file-path)]
    (loop [part-index 0]
      (let [buffer (byte-array CHUNK_SIZE_BYTES)
            bytes-read (.read reader buffer)]
        (when (pos? bytes-read)
          (let [part-file (io/file "/tmp" (str "part-" part-index "-" file-id ".bin"))]
            (with-open [writer (io/output-stream part-file)]
              (.write writer buffer 0 bytes-read)))
          (recur (inc part-index))))))
  (let [fragments-files (->> (file-seq (io/file "/tmp/"))
                             (filter #(clojure.string/includes? (.getAbsolutePath %) (str file-id)))
                             (sort-by #(.getAbsolutePath %)))]
    (mapv (fn [fragment-file]
            (let [output-encrypted-fragment-file (-> (.getAbsolutePath fragment-file)
                                                     (clojure.string/replace ".bin" ".enc")
                                                     io/file)]
              (cryptograph/encrypt-file! fragment-file output-encrypted-fragment-file (str file-id))
              output-encrypted-fragment-file))
          fragments-files)))

(s/defn ^:private delete-files!
  [file-id :- s/Uuid]
  (let [files (->> (file-seq (io/file "/tmp/"))
                   (filter #(clojure.string/includes? (.getAbsolutePath %) (str file-id)))
                   (sort-by #(.getAbsolutePath %)))]
    (doseq [file files]
      (io/delete-file file))))

(s/defn upload-file!
  [absolute-file-path :- s/Str
   file-id :- s/Uuid
   postgresql
   {:keys [telegram]}
   http-client]
  (let [{:file/keys [callback-url]} (database.file/lookup file-id postgresql)
        total-file-size (-> (io/file absolute-file-path) .length)
        file-hash (-> (io/file absolute-file-path) hash/md5 buddy-codecs/bytes->hex)
        files (split-file! absolute-file-path file-id)
        fragments (controllers.fragment/process-fragments-upload! file-id files (:chat-id telegram) (:token telegram) http-client)]
    (database.file/upload-completed! file-id total-file-size file-hash fragments postgresql)
    (when callback-url
      ;; TODO: Implement a retry policy
      (diplomat.http-client/hit-callback-url! callback-url http-client))
    ;; TODO: Guarantee that the files will be deleted even of there was an exception in the middle of the file upload process
    (delete-files! file-id)))

(s/defn download-file! :- File
  [file-id :- s/Uuid
   {:keys [telegram]}
   database-connection
   http-client]
  (controllers.fragment/fragments->file! file-id (:token telegram) database-connection http-client))
