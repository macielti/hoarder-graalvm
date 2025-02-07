(ns hoarder-graalvm.diplomat.db.postgresql.file
  (:require [hoarder-graalvm.adapters.file :as adapters.file]
            [hoarder-graalvm.models.file :as models.file]
            [medley.core :as medley]
            [pg.core :as pg]
            [schema.core :as s]))

(s/defn insert! :- models.file/File
  [{:file/keys [id name total-size access-count hash callback-url created-at]} :- models.file/File
   pool]
  (pg/with-connection [connection pool]
    (->> (pg/execute connection
                     "INSERT INTO customers (id, name, total_size, access_count, hash, callback_url, created_at)
                      VALUES ($1, $2, $3, $4, $5, $6, $7)
                     RETURNING *"
                     {:params [id name total-size access-count hash callback-url created-at]
                      :first  true})
         (medley/remove-vals nil?)
         adapters.file/postgresql->internal)))
