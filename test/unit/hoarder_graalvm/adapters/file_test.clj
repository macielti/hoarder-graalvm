(ns hoarder-graalvm.adapters.file-test
  (:require [clojure.test :refer [is testing]]
            [fixtures.file]
            [hoarder-graalvm.adapters.file :as adapters.file]
            [java-time.api :as jt]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

(s/deftest internal->wire-test
  (testing "Given a internal File entity, we should be able to externalize it to wire layer"
    (is (= {:id           (str fixtures.file/file-id)
            :name         fixtures.file/file-name
            :access-count 0
            :created-at   (str fixtures.file/file-created-at)}
           (adapters.file/internal->wire fixtures.file/internal-file)))

    (is (= {:id           (str fixtures.file/file-id)
            :name         fixtures.file/file-name
            :callback-url "http://example.com"
            :hash         "example-hash"
            :total-size   100
            :access-count 0
            :created-at   (str fixtures.file/file-created-at)}
           (adapters.file/internal->wire fixtures.file/complete-internal-file)))))

(s/deftest wire->internal-test
  (testing "Given a wire File entry, we should be able to internalize it to internal layer"
    (is (match? {:file/id           uuid?
                 :file/name         fixtures.file/file-name
                 :file/access-count 0
                 :file/created-at   jt/local-date-time?}
                (adapters.file/wire->internal {:name fixtures.file/file-name})))

    (is (match? {:file/id           uuid?
                 :file/name         fixtures.file/file-name
                 :file/access-count 0
                 :file/created-at   jt/local-date-time?
                 :file/callback-url "http://example.com"}
                (adapters.file/wire->internal {:name         fixtures.file/file-name
                                               :callback-url "http://example.com"})))))

(s/deftest postgresql->internal-test
  (testing "Given a postgresql File entity, we should be able to internalize it to internal layer"
    (is (= {:file/id           fixtures.file/file-id
            :file/name         fixtures.file/file-name
            :file/access-count 0
            :file/created-at   fixtures.file/file-created-at}
           (adapters.file/postgresql->internal fixtures.file/postgresql-file)))

    (is (= {:file/id           fixtures.file/file-id
            :file/name         fixtures.file/file-name
            :file/access-count 0
            :file/created-at   fixtures.file/file-created-at
            :file/hash         "example-hash"
            :file/callback-url "http://example.com"
            :file/total-size   100}
           (adapters.file/postgresql->internal fixtures.file/complete-postgresql-file)))))
