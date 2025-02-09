(ns hoarder-graalvm.diplomat.db.postgresql.fragment-test
  (:require [aux.components]
            [clojure.test :refer [is testing]]
            [common-test-clj.component.postgresql-mock :as component.postgresql-mock]
            [common-test-clj.helpers.schema :as helpers.schema]
            [fixtures.file]
            [fixtures.fragment]
            [hoarder-graalvm.diplomat.db.postgresql.fragment :as database.fragment]
            [hoarder-graalvm.models.fragment :as models.fragment]
            [matcher-combinators.test :refer [match?]]
            [pg.core :as pg]
            [schema.test :as s]))

(s/deftest insert!-test
  (let [pool (component.postgresql-mock/postgresql-pool-mock aux.components/schemas)]
    (testing "Given a Fragment entity, we should be able to insert it into the database"
      (is (match? fixtures.fragment/internal-fragment
                  (pg/with-connection [connection pool]
                    (database.fragment/insert! fixtures.fragment/internal-fragment connection)))))))

(s/deftest by-file-id-test
  (let [pool (component.postgresql-mock/postgresql-pool-mock aux.components/schemas)
        fragment-1 fixtures.fragment/internal-fragment
        fragment-2 (helpers.schema/generate models.fragment/Fragment {:fragment/file-id fixtures.file/file-id})]
    (testing "Given a file id, we should be able to retrieve all fragments related to it"
      (is (match? fragment-1
                  (pg/with-connection [connection pool]
                    (database.fragment/insert! fragment-1 connection))))

      (is (match? fragment-2
                  (pg/with-connection [connection pool]
                    (database.fragment/insert! fragment-2 connection))))

      (is (match? {:fragment/id uuid?}
                  (pg/with-connection [connection pool]
                    (-> (helpers.schema/generate models.fragment/Fragment {})
                        (database.fragment/insert! connection)))))

      (is (match? {:fragment/id uuid?}
                  (pg/with-connection [connection pool]
                    (-> (helpers.schema/generate models.fragment/Fragment {})
                        (database.fragment/insert! connection)))))

      (is (match? {:fragment/id uuid?}
                  (pg/with-connection [connection pool]
                    (-> (helpers.schema/generate models.fragment/Fragment {})
                        (database.fragment/insert! connection)))))

      (is (match? [fragment-1 fragment-2]
                  (pg/with-connection [connection pool]
                    (database.fragment/by-file-id fixtures.file/file-id connection)))))))
