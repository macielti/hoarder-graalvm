(ns hoarder-graalvm.wire.in.database.postgresql.fragment
  (:require [schema.core :as s])
  (:import (java.time LocalDateTime)))

(s/defschema Fragment
  {:id               s/Uuid
   :file_id          s/Uuid
   :external_file_id s/Str
   :index            s/Int
   :hash             s/Str
   :created_at       LocalDateTime})
