(ns fixtures.fragment
  (:require [common-test-clj.helpers.schema :as helpers.schema]
            [fixtures.file]
            [hoarder-graalvm.models.fragment :as models.fragment]
            [hoarder-graalvm.wire.in.database.postgresql.fragment :as wire.in.database.fragment]
            [java-time.api :as jt]
            [schema.core :as s]))

(def fragment-id (random-uuid))
(def external-file-id (str (random-uuid)))

(s/def internal-fragment :- models.fragment/Fragment
  (helpers.schema/generate models.fragment/Fragment
                           {:fragment/id               fragment-id
                            :fragment/file-id          fixtures.file/file-id
                            :fragment/external-file-id external-file-id
                            :fragment/index            0
                            :fragment/hash             fixtures.file/file-hash
                            :fragment/created-at       (jt/local-date-time)}))

(s/def postgresql-fragment :- wire.in.database.fragment/Fragment
  (helpers.schema/generate wire.in.database.fragment/Fragment
                           {:id               fragment-id
                            :file_id          fixtures.file/file-id
                            :external_file_id external-file-id
                            :index            0
                            :hash             fixtures.file/file-hash
                            :created_at       (jt/local-date-time)}))
