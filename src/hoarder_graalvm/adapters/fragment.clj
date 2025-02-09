(ns hoarder-graalvm.adapters.fragment
  (:require [hoarder-graalvm.models.fragment :as models.fragment]
            [hoarder-graalvm.wire.in.database.postgresql.fragment :as wire.in.database.fragment]
            [schema.core :as s]))

(s/defn postgresql->internal :- models.fragment/Fragment
  [fragment :- wire.in.database.fragment/Fragment]
  {:fragment/id               (:id fragment)
   :fragment/file-id          (:file_id fragment)
   :fragment/external-file-id (:external_file_id fragment)
   :fragment/index            (:index fragment)
   :fragment/hash             (:hash fragment)
   :fragment/created-at       (:created_at fragment)})
