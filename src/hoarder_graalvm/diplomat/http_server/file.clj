(ns hoarder-graalvm.diplomat.http-server.file
  (:require [hoarder-graalvm.adapters.file :as adapters.file]
            [hoarder-graalvm.controllers.file :as controllers.file]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn create-file!
  [{{:keys [file]}       :json-params
    {:keys [postgresql]} :components}]
  {:status 200
   :body   {:file (-> (adapters.file/wire->internal file)
                      (controllers.file/create-file! postgresql)
                      adapters.file/internal->wire)}})

(s/defn fetch-file
  [{{:keys [file-id]}    :path-params
    {:keys [postgresql]} :components}]
  {:status 200
   :body   {:file (-> (UUID/fromString file-id)
                      (controllers.file/fetch-file postgresql)
                      adapters.file/internal->wire)}})

(s/defn upload-file!
  [{params                                  :params
    {:keys [file-id]}                       :path-params
    {:keys [postgresql config http-client]} :components}]
  (let [absolute-file-path (-> (get-in params ["file" :tempfile]) .getAbsolutePath)
        file-id' (UUID/fromString file-id)]
    (controllers.file/upload-file! absolute-file-path file-id' postgresql config http-client)
    {:status 200}))
