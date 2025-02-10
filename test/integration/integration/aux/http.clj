(ns integration.aux.http
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [clojure.java.io :as io]
            [io.pedestal.test :as test]))

(defn create-file!
  [{:keys [file]}
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :post "/api/files"
                                                 :headers {"Content-Type" "application/json"}
                                                 :body (json/encode {:file file}))]
    {:status status
     :body   (json/decode body true)}))

(defn upload-file!
  [file-id
   file-path
   service-port]
  (let [{:keys [body status]} (client/post (str "http://localhost:" service-port "/api/files/" file-id "/upload")
                                           {:multipart        [{:name "file" :content (io/file file-path)}]
                                            :throw-exceptions false})]
    {:status status
     :body   (json/decode body true)}))

(defn fetch-file
  [file-id
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :get (str "/api/files/" file-id))]
    {:status status
     :body   (json/decode body true)}))

(defn fetch-fragments-by-file
  [file-id
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn :get (str "/api/files/" file-id "/fragments"))]
    {:status status
     :body   (json/decode body true)}))
