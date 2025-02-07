(ns hoarder-graalvm.components
  (:require [common-clj.integrant-components.config :as component.config]
            [common-clj.integrant-components.routes :as component.routes]
            [hoarder-graalvm.diplomat.http-server :as diplomat.http-server]
            [http-client-component.core :as component.http-client]
            [integrant.core :as ig]
            [new-relic-component.core :as component.new-relic]
            [postgresql-component.core :as component.postgresql]
            [prometheus-component.core :as component.prometheus]
            [service-component.core :as component.service]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.tools.logging])
  (:gen-class))

(taoensso.timbre.tools.logging/use-timbre)

(def dependencies
  {:config      (ig/ref ::component.config/config)
   :prometheus  (ig/ref ::component.prometheus/prometheus)
   :postgresql  (ig/ref ::component.postgresql/postgresql)
   :http-client (ig/ref ::component.http-client/http-client)})

(def components
  {::component.config/config           {:path "resources/config.edn"
                                        :env  :prod}
   ::component.postgresql/postgresql   {:components {:config (ig/ref ::component.config/config)}}
   ::component.prometheus/prometheus   {:metrics []}
   ::component.http-client/http-client {:components {:config     (ig/ref ::component.config/config)
                                                     :prometheus (ig/ref ::component.prometheus/prometheus)}}
   ::component.new-relic/new-relic     {:components {:config      (ig/ref ::component.config/config)
                                                     :http-client (ig/ref ::component.http-client/http-client)}}
   ::component.routes/routes           {:routes diplomat.http-server/routes}
   ::component.service/service         {:components (merge dependencies
                                                           {:routes (ig/ref ::component.routes/routes)})}})

(defn start-system! []
  (timbre/set-min-level! :debug)
  (ig/init components))

(def -main start-system!)
