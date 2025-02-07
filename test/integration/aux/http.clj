(ns aux.http
  (:require [cheshire.core :as json]
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

(defn fetch-file
  [file-id
   service-fn]
  (let [{:keys [body status]} (test/response-for service-fn
                                                 :get (str "/api/files/" file-id))]
    {:status #p status
     :body   (json/decode #p body true)}))
