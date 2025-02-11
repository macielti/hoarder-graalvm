(ns hoarder-graalvm.logic.file
  (:require [schema.core :as s]))

(s/defn file-path-for-joined-fragments-files :- s/Str
  [file-id :- s/Uuid]
  (format "/tmp/%s-complete" file-id))
