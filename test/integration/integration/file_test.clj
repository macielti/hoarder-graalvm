(ns integration.file-test
  (:require [buddy.core.codecs :as buddy-codecs]
            [buddy.core.hash :as hash]
            [cheshire.core :as json]
            [clj-uuid]
            [clojure.java.io :as io]
            [clojure.test :refer [is testing]]
            [fixtures.file]
            [http-client-component.core :as component.http-client]
            [integrant.core :as ig]
            [integration.aux.components :as aux.components]
            [integration.aux.http :as aux.http]
            [matcher-combinators.test :refer [match?]]
            [org.httpkit.fake :as http-fake]
            [schema.test :as s]
            [service-component.core :as component.service]))

(defn mocked-http-responses-map->list
  [http-responses-map]
  (-> (into [] http-responses-map) flatten))

(def mocked-http-responses
  {{:url #"https:\/\/api\.telegram\.org\/bot[0-9]+:[A-Za-z0-9_-]+\/sendDocument" :method :post}
   (fn [_orig-fn _opts _callback]
     {:status 200
      :body   (json/encode {:result {:document {:file_id (str (random-uuid))}}})})})

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

(s/deftest file-upload-test
  (let [system (aux.components/start-system!)
        http-client (-> system ::component.http-client/http-client)
        service-fn (-> system ::component.service/service :io.pedestal.http/service-fn)
        service-port (-> system ::component.service/service :io.pedestal.http/port)
        file-id (-> (aux.http/create-file! {:file {:name fixtures.file/file-name}} service-fn)
                    :body
                    :file
                    :id)]

    (http-fake/with-fake-http (mocked-http-responses-map->list mocked-http-responses)
      (testing "That we can upload a file"
        (is (match? {:status 200}
                    (aux.http/upload-file! file-id "README.md" service-port)))))

    (testing "The file entity has the correct file size and hash"
      (is (match? {:body {:file {:id         (str file-id)
                                 :total-size int?
                                 :hash       #(= % (-> (io/file "README.md") hash/md5 buddy-codecs/bytes->hex))}}}
                  (aux.http/fetch-file file-id service-fn))))

    (testing "Performed request to Telegram API"
      (is (match? [{:url "https://log-api.newrelic.com/log/v1" :method :post}
                   {:url "https://api.telegram.org/bot123456:random-token/sendDocument" :method :post}]
                  (component.http-client/requests http-client))))

    (testing "Should be able to fetch created Fragments during File upload flow"
      (is (match? {:status 200
                   :body   {:fragments [{:id clj-uuid/uuid-string?}]}}
                  (aux.http/fetch-fragments-by-file file-id service-fn))))

    (ig/halt! system)))
