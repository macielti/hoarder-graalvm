(ns hoarder-graalvm.diplomat.http-server.fragment
  (:require [hoarder-graalvm.adapters.fragment :as adapters.fragment]
            [hoarder-graalvm.controllers.fragment :as controllers.fragment]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn by-file
  [{{:keys [file-id]}    :path-params
    {:keys [postgresql]} :components}]
  {:status 200
   :body   {:fragments (->> (controllers.fragment/by-file (UUID/fromString file-id) postgresql)
                            (map adapters.fragment/internal->wire))}})
