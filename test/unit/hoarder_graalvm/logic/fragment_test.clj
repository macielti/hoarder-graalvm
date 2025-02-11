(ns hoarder-graalvm.logic.fragment-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer [is testing]]
            [fixtures.file]
            [fixtures.fragment]
            [hoarder-graalvm.logic.fragment :as logic.fragment]
            [java-time.api :as jt]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

(s/deftest ->fragment-test
  (testing "Should be able to compose a Fragment entity"
    (is (match? {:fragment/id               uuid?
                 :fragment/external-file-id string?
                 :fragment/file-id          fixtures.file/file-id
                 :fragment/hash             "42f8f465841623bc7554d391ca28c5a3"
                 :fragment/index            0
                 :fragment/created-at       jt/local-date-time?}
                (logic.fragment/->fragment fixtures.fragment/external-file-id fixtures.file/file-id 0 (io/file "README.md"))))))

(s/deftest output-fragment-file-path-test
  (testing "Given a Fragment entity, it should return the path to the file where the fragment File is stored"
    (is (match? "/tmp/07939371-3d12-49b5-9484-298cd817d8c6-download-part-0"
                (logic.fragment/output-fragment-file-path (assoc fixtures.fragment/internal-fragment
                                                                 :fragment/file-id #uuid "07939371-3d12-49b5-9484-298cd817d8c6"
                                                                 :fragment/index 0))))))
