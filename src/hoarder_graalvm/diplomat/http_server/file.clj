(ns hoarder-graalvm.diplomat.http-server.file
  (:require [hoarder-graalvm.adapters.file :as adapters.file]
            [hoarder-graalvm.controllers.file :as controllers.file]
            [schema.core :as s]))

(s/defn create-file!
  [{{:keys [file]}       :json-params
    {:keys [postgresql]} :components}]
  {:status 200
   :body   {:file (-> (adapters.file/wire->internal file)
                      (controllers.file/create-file! postgresql)
                      adapters.file/internal->wire)}})
