(ns fixtures.file
  (:require [common-test-clj.helpers.schema :as helpers.schema]
            [hoarder-graalvm.models.file :as models.file]
            [hoarder-graalvm.wire.in.database.postgresql.file :as wre.in.database.file]
            [java-time.api :as jt]
            [schema.core :as s]))

(def file-id (random-uuid))
(def file-name "File Name Example.pdf")
(def file-created-at (jt/local-date-time))
(def file-hash (str (random-uuid)))

(def internal-file
  (helpers.schema/generate models.file/File
                           {:file/id           file-id
                            :file/name         file-name
                            :file/access-count 0
                            :file/created-at   file-created-at}))

(s/def complete-internal-file :- models.file/File
  (merge internal-file
         {:file/total-size   100
          :file/hash         "example-hash"
          :file/callback-url "http://example.com"}))

(s/def postgresql-file
  (helpers.schema/generate wre.in.database.file/File
                           {:id           file-id
                            :name         file-name
                            :access_count 0
                            :created_at   file-created-at}))

(s/def complete-postgresql-file
  (helpers.schema/generate wre.in.database.file/File
                           {:id           file-id
                            :name         file-name
                            :access_count 0
                            :created_at   file-created-at
                            :total_size   100
                            :hash         "example-hash"
                            :callback_url "http://example.com"}))
