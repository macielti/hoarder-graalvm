(ns hoarder-graalvm.wire.in.file
  (:require [schema.core :as s]))

(s/defschema File
  {:name                          s/Str
   (s/optional-key :callback-url) s/Str})
