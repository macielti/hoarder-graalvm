(ns hoarder-graalvm.models.fragment
  (:require [schema.core :as s])
  (:import (java.time LocalDateTime)))

(s/defschema Fragment
  {:fragment/id               s/Uuid
   :fragment/file-id          s/Uuid
   :fragment/external-file-id s/Str
   :fragment/index            s/Int
   :fragment/hash             s/Str
   :fragment/created-at       LocalDateTime})
