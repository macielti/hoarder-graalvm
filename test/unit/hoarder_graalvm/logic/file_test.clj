(ns hoarder-graalvm.logic.file-test
  (:require [clj-uuid]
            [clojure.test :refer [is testing]]
            [hoarder-graalvm.logic.file :as logic.file]
            [schema.test :as s]))

(s/deftest joined-fragments-file-path-test
  (testing "Given a file-id, it should return the path to the file where the fragments are joined"
    (is (= "/tmp/df9e8a9d-54fd-4d34-9317-3bdfdee7630b-complete"
           (logic.file/joined-fragments-file-path #uuid "df9e8a9d-54fd-4d34-9317-3bdfdee7630b")))))
