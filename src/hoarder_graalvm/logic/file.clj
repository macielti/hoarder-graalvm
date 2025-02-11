(ns hoarder-graalvm.logic.file
  (:require [schema.core :as s]))

(s/defn joined-fragments-file-path :- s/Str
  [file-id :- s/Uuid]
  (format "/tmp/%s-complete" file-id))
