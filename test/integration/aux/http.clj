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
