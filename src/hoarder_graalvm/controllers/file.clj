(ns hoarder-graalvm.controllers.file
  (:require [hoarder-graalvm.diplomat.db.postgresql.file :as database.file]
            [hoarder-graalvm.models.file :as models.file]
            [schema.core :as s]))

(s/defn create-file! :- models.file/File
  [file :- models.file/File
   pool]
  (database.file/insert! file pool))

(s/defn fetch-file :- (s/maybe models.file/File)
  [file-id :- s/Uuid
   pool]
  (database.file/lookup file-id pool))
