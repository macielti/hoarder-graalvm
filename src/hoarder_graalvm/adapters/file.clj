(ns hoarder-graalvm.adapters.file
  (:require [hoarder-graalvm.models.file :as models.file]
            [hoarder-graalvm.wire.in.database.postgresql.file :as wire.in.database.file]
            [hoarder-graalvm.wire.in.file :as wire.in.file]
            [hoarder-graalvm.wire.out.file :as wire.out.file]
            [java-time.api :as jt]
            [medley.core :as medley]
            [schema.core :as s]))

(s/defn internal->wire :- wire.out.file/File
  [{:file/keys [id name total-size access-count hash created-at callback-url]} :- models.file/File]
  (medley/assoc-some {:id           (str id)
                      :name         name
                      :access-count (int access-count)
                      :created-at   (str created-at)}
                     :callback-url callback-url
                     :total-size (when total-size (int total-size))
                     :hash hash))

(s/defn wire->internal :- models.file/File
  [{:keys [name callback-url]} :- wire.in.file/File]
  (medley/assoc-some {:file/id           (random-uuid)
                      :file/name         name
                      :file/access-count 0
                      :file/created-at   (jt/local-date-time)}
                     :file/callback-url callback-url))

(s/defn postgresql->internal :- models.file/File
  [file :- wire.in.database.file/File]
  (medley/assoc-some {:file/id           (:id file)
                      :file/name         (:name file)
                      :file/access-count (:access_count file)
                      :file/created-at   (:created_at file)}
                     :file/total-size (:total_size file)
                     :file/hash (:hash file)
                     :file/callback-url (:callback_url file)))
