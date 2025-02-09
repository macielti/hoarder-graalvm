(ns hoarder-graalvm.diplomat.db.postgresql.file
  (:require [hoarder-graalvm.adapters.file :as adapters.file]
            [hoarder-graalvm.diplomat.db.postgresql.fragment :as database.fragment]
            [hoarder-graalvm.models.file :as models.file]
            [hoarder-graalvm.models.fragment :as models.fragment]
            [medley.core :as medley]
            [pg.core :as pg]
            [schema.core :as s])
  (:import (org.pg Connection Pool)))

(s/defn insert! :- models.file/File
  [{:file/keys [id name total-size access-count hash callback-url created-at]} :- models.file/File
   pool]
  (pg/with-connection [connection pool]
    (->> (pg/execute connection
                     "INSERT INTO files (id, name, total_size, access_count, hash, callback_url, created_at)
                      VALUES ($1, $2, $3, $4, $5, $6, $7)
                     RETURNING *"
                     {:params [id name total-size access-count hash callback-url created-at]
                      :first  true})
         (medley/remove-vals nil?)
         adapters.file/postgresql->internal)))

(s/defn lookup :- (s/maybe models.file/File)
  [id :- s/Uuid
   pool]
  (pg/with-connection [connection pool]
    (some->> (pg/execute connection
                         "SELECT * FROM files WHERE id = $1"
                         {:params [id]
                          :first  true})
             (medley/remove-vals nil?)
             adapters.file/postgresql->internal)))

(s/defn set-size-and-hash! :- models.file/File
  [file-id :- s/Uuid
   file-total-size :- s/Int
   file-hash :- s/Str
   connection :- Connection]
  (->> (pg/execute connection
                   "UPDATE files SET total_size = $1, hash = $2 WHERE id = $3
                   RETURNING *"
                   {:params [file-total-size file-hash file-id]
                    :first  true})
       (medley/remove-vals nil?)
       adapters.file/postgresql->internal))

(s/defn upload-completed!
  [file-id :- s/Uuid
   file-total-size :- s/Int
   file-hash :- s/Str
   fragments :- [models.fragment/Fragment]
   pool :- Pool]
  (pg/with-connection [database-conn pool]
    (pg/with-transaction [database-conn' database-conn {:isolation-level :serializable}]
      (set-size-and-hash! file-id file-total-size file-hash database-conn')
      (doseq [fragment fragments]
        (database.fragment/insert! fragment database-conn')))))
