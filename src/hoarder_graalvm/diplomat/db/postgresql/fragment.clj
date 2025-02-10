(ns hoarder-graalvm.diplomat.db.postgresql.fragment
  (:require [hoarder-graalvm.adapters.fragment :as adapters.fragment]
            [hoarder-graalvm.models.fragment :as models.fragment]
            [medley.core :as medley]
            [pg.core :as pg]
            [schema.core :as s])
  (:import (org.pg Connection)))

(s/defn insert! :- models.fragment/Fragment
  [{:fragment/keys [id file-id external-file-id index hash created-at]} :- models.fragment/Fragment
   connection :- Connection]
  (->> (pg/execute connection
                   "INSERT INTO fragments (id, file_id, external_file_id, index, hash, created_at)
                    VALUES ($1, $2, $3, $4, $5, $6)
                   RETURNING *"
                   {:params [id file-id external-file-id index hash created-at]
                    :first  true})
       (medley/remove-vals nil?)
       adapters.fragment/postgresql->internal))

(s/defn by-file-id :- [models.fragment/Fragment]
  [file-id :- s/Uuid
   postgresql-pool]
  (pg/with-connection [connection postgresql-pool]
    (->> (pg/execute connection
                     "SELECT *
                      FROM fragments
                      WHERE file_id = $1"
                     {:params [file-id]
                      :map    (fn [row] (medley/remove-vals nil? row))})
         (mapv adapters.fragment/postgresql->internal))))
