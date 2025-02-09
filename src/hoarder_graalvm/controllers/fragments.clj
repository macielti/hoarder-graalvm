(ns hoarder-graalvm.controllers.fragments
  (:require [hoarder-graalvm.diplomat.telegram.producer :as diplomat.telegram.producer]
            [hoarder-graalvm.logic.fragment :as logic.fragment]
            [hoarder-graalvm.models.fragment :as models.fragment]
            [schema.core :as s])
  (:import (java.io File)))

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
