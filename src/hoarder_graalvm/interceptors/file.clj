(ns hoarder-graalvm.interceptors.file
  (:require [hoarder-graalvm.diplomat.db.postgresql.fragment :as database.fragment]
            [io.pedestal.interceptor :as pedestal.interceptor]
            [postgresql-component.interceptors :as database.interceptors]
            [service-component.error :as common-error])
  (:import (java.util UUID)))

(defn file-identifier-fn
  [{{:keys [path-params]} :request}]
  (-> path-params :file-id UUID/fromString))

(def file-existence-check-interceptor
  (database.interceptors/resource-existence-check-interceptor file-identifier-fn
                                                              "SELECT * FROM files WHERE id = $1"))

(def already-uploaded-file-check-interceptor
  (pedestal.interceptor/interceptor
   {:name  ::already-uploaded-file-check-interceptor
    :enter (fn [{{:keys [components path-params]} :request :as context}]
             (let [file-id (-> path-params :file-id UUID/fromString)
                   fragments (database.fragment/by-file-id file-id (:postgresql components))]
               (when-not (empty? fragments)
                 (common-error/http-friendly-exception 400
                                                       "resource-already-exists"
                                                       "File entity already received a valid upload"
                                                       {:file-id file-id})))
             context)}))
