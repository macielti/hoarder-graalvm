(ns hoarder-graalvm.wire.out.file
  (:require [schema.core :as s]))

(s/defschema File
  {:id                            s/Str
   :name                          s/Str
   (s/optional-key :total-size)   s/Int
   :access-count                  s/Int
   (s/optional-key :hash)         s/Str
   :created-at                    s/Str
   (s/optional-key :callback-url) s/Str})
