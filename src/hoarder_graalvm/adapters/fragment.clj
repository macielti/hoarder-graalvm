(ns hoarder-graalvm.adapters.fragment
  (:require [hoarder-graalvm.models.fragment :as models.fragment]
            [hoarder-graalvm.wire.in.database.postgresql.fragment :as wire.in.database.fragment]
            [hoarder-graalvm.wire.out.fragment :as wire.out.fragment]
            [schema.core :as s]))

(s/defn postgresql->internal :- models.fragment/Fragment
  [fragment :- wire.in.database.fragment/Fragment]
  {:fragment/id               (:id fragment)
   :fragment/file-id          (:file_id fragment)
   :fragment/external-file-id (:external_file_id fragment)
   :fragment/index            (:index fragment)
   :fragment/hash             (:hash fragment)
   :fragment/created-at       (:created_at fragment)})

(s/defn internal->wire :- wire.out.fragment/Fragment
  [{:fragment/keys [id file-id external-file-id index hash created-at]} :- models.fragment/Fragment]
  {:id               (str id)
   :file-id          (str file-id)
   :external-file-id external-file-id
   :index            index
   :hash             hash
   :created-at       (str created-at)})
