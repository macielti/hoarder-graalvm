(ns hoarder-graalvm.models.file
  (:require
   [schema.core :as s])
  (:import
   (java.time LocalDate)))

(s/defschema File
  {:file/id                            s/Uuid
   :file/name                          s/Str
   (s/optional-key :file/total-size)   s/Int
   :file/access-count                  s/Int
   (s/optional-key :file/hash)         s/Str
   (s/optional-key :file/callback-url) s/Str
   :file/created-at                    LocalDate})
