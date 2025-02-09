(ns hoarder-graalvm.diplomat.telegram.producer
  (:require [hoarder-graalvm.telegram :as telegram]
            [schema.core :as s])
  (:import (java.io File)))

(s/defn send-document! :- s/Str
  [file :- File
   chat-id :- s/Str
   token :- s/Str
   http-client]
  (-> (telegram/send-file! token chat-id {} file "/sendDocument" "document" (str (random-uuid)) http-client)
      :result
      :document
      :file_id))
