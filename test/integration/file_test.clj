(ns file-test
  (:require [aux.components]
            [aux.http]
            [clj-uuid]
            [clojure.test :refer [is testing]]
            [fixtures.file]
            [integrant.core :as ig]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]
            [service-component.core :as component.service]))

(s/deftest file-creation-test
  (let [system (aux.components/start-system!)
        service-fn (-> system ::component.service/service :io.pedestal.http/service-fn)]
    (testing "That we can create a file entity"
      (is (match? {:body   {:file {:id           clj-uuid/uuid-string?
                                   :name         fixtures.file/file-name
                                   :access-count 0
                                   :created-at   string?}}
                   :status 200}
                  (aux.http/create-file! {:file {:name fixtures.file/file-name}} service-fn))))
    (ig/halt! system)))

(s/deftest fetch-file-test
  (let [system (aux.components/start-system!)
        service-fn (-> system ::component.service/service :io.pedestal.http/service-fn)
        file-id (-> (aux.http/create-file! {:file {:name fixtures.file/file-name}} service-fn)
                    :body
                    :file
                    :id)]

    (testing "That we can fetch a file entity"
      (is (match? {:body   {:file {:id           clj-uuid/uuid-string?
                                   :name         fixtures.file/file-name
                                   :access-count 0
                                   :created-at   string?}}
                   :status 200}
                  (aux.http/fetch-file file-id service-fn))))
    (ig/halt! system)))
