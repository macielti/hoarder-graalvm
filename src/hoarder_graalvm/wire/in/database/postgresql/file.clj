(ns hoarder-graalvm.wire.in.database.postgresql.file
  (:require [schema.core :as s])
  (:import (java.time LocalDateTime)))

(s/defschema File
  {:id                            s/Uuid
   :name                          s/Str
   (s/optional-key :total_size)   s/Int
   :access_count                  s/Int
   (s/optional-key :hash)         s/Str
   (s/optional-key :callback_url) s/Str
   :created_at                    LocalDateTime})
