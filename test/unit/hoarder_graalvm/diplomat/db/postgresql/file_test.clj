(ns hoarder-graalvm.diplomat.db.postgresql.file-test
  (:require [aux.components]
            [clj-uuid]
            [clojure.test :refer [is testing]]
            [common-test-clj.component.postgresql-mock :as component.postgresql-mock]
            [fixtures.file]
            [hoarder-graalvm.diplomat.db.postgresql.file :as database.file]
            [java-time.api :as jt]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

(s/deftest insert!-test
  (let [pool (component.postgresql-mock/postgresql-pool-mock aux.components/schemas)]
    (testing "Given a file entity, we should be able to insert it into the database"
      (is (match? {:file/id           uuid?
                   :file/access-count 0
                   :file/name         fixtures.file/file-name
                   :file/created-at   jt/local-date-time?}
                  (database.file/insert! fixtures.file/internal-file pool))))))

(s/deftest lookup-test
  (let [pool (component.postgresql-mock/postgresql-pool-mock aux.components/schemas)]
    (testing "Given a file id, we should be able to lookup the file entity from the database"
      (is (match? {:file/id uuid?}
                  (database.file/insert! fixtures.file/internal-file pool)))

      (is (match? {:file/id           uuid?
                   :file/access-count 0
                   :file/name         fixtures.file/file-name
                   :file/created-at   jt/local-date-time?}
                  (database.file/lookup fixtures.file/file-id pool))))))
