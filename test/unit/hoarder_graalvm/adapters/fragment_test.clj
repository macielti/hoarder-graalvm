(ns hoarder-graalvm.adapters.fragment-test
  (:require [clojure.test :refer [is testing]]
            [fixtures.file]
            [clj-uuid]
            [fixtures.fragment]
            [hoarder-graalvm.adapters.fragment :as adapters.fragment]
            [java-time.api :as jt]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

(s/deftest postgresql->internal-test
  (testing "Given a fragment entity from the database, we should be able to convert it to an internal fragment entity"
    (is (match? {:fragment/id               fixtures.fragment/fragment-id
                 :fragment/file-id          fixtures.file/file-id
                 :fragment/external-file-id string?
                 :fragment/hash             string?
                 :fragment/index            0
                 :fragment/created-at       jt/local-date-time?}
                (adapters.fragment/postgresql->internal fixtures.fragment/postgresql-fragment)))))

(s/deftest internal->wire-test
  (testing "Given an internal Fragment entity, we should be able to convert it to a wire Fragment entity"
    (is (match? {:id               clj-uuid/uuid-string?
                 :file-id          clj-uuid/uuid-string?
                 :external-file-id string?
                 :index            0
                 :hash             string?
                 :created-at       string?}
                (adapters.fragment/internal->wire fixtures.fragment/internal-fragment)))))
