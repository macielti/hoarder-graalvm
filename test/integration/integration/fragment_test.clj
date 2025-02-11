(ns integration.fragment-test
  (:require [clojure.test :refer [is testing]]
            [fixtures.file]
            [integrant.core :as ig]
            [integration.aux.components :as aux.components]
            [integration.aux.http :as aux.http]
            [schema.test :as s]
            [service-component.core :as component.service]))

(s/deftest fetch-fragments-by-file-test
  (let [system (aux.components/start-system!)
        service-fn (-> system ::component.service/service :io.pedestal.http/service-fn)
        file-id (-> (aux.http/create-file! {:file {:name fixtures.file/file-name}} service-fn)
                    :body
                    :file
                    :id)]

    (testing "When file id does not exists, we should get a 404 response"
      (is (= {:status 404
              :body   {:detail  "Not Found"
                       :error   "resource-not-found"
                       :message "Resource could not be found"}}
             (aux.http/fetch-fragments-by-file (random-uuid) service-fn))))

    (testing "When the file id exists, but there was not a upload yet"
      (is (= {:status 200
              :body   {:fragments []}}
             (aux.http/fetch-fragments-by-file file-id service-fn))))

    (ig/halt! system)))
