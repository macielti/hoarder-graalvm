(ns integration.aux.components
  (:require [common-clj.integrant-components.config :as component.config]
            [common-clj.integrant-components.routes :as component.routes]
            [common-test-clj.component.postgresql-mock :as component.postgresql-mock]
            [hoarder-graalvm.diplomat.http-server :as diplomat.http-server]
            [http-client-component.core :as component.http-client]
            [integrant.core :as ig]
            [new-relic-component.core :as component.new-relic]
            [prometheus-component.core :as component.prometheus]
            [service-component.core :as component.service]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.tools.logging]))

(taoensso.timbre.tools.logging/use-timbre)

(def schemas ["CREATE TABLE IF NOT EXISTS files (id UUID PRIMARY KEY, name VARCHAR NOT NULL, total_size INTEGER, access_count INTEGER NOT NULL, hash VARCHAR, callback_url VARCHAR, created_at TIMESTAMP NOT NULL);"
              "CREATE TABLE IF NOT EXISTS fragments (id UUID PRIMARY KEY, file_id UUID NOT NULL, external_file_id VARCHAR NOT NULL, index INTEGER NOT NULL, hash VARCHAR NOT NULL, created_at TIMESTAMP NOT NULL);"])

(def dependencies
  {:config      (ig/ref ::component.config/config)
   :prometheus  (ig/ref ::component.prometheus/prometheus)
   :postgresql  (ig/ref ::component.postgresql-mock/postgresql-mock)
   :http-client (ig/ref ::component.http-client/http-client)})

(def components
  {::component.config/config                   {:path "resources/config.example.edn"
                                                :env  :test}
   ::component.postgresql-mock/postgresql-mock {:schemas    schemas
                                                :components {:config (ig/ref ::component.config/config)}}
   ::component.prometheus/prometheus           {:metrics []}
   ::component.http-client/http-client         {:components {:config     (ig/ref ::component.config/config)
                                                             :prometheus (ig/ref ::component.prometheus/prometheus)}}
   ::component.new-relic/new-relic             {:components {:config      (ig/ref ::component.config/config)
                                                             :http-client (ig/ref ::component.http-client/http-client)}}
   ::component.routes/routes                   {:routes diplomat.http-server/routes}
   ::component.service/service                 {:components (merge dependencies
                                                                   {:routes (ig/ref ::component.routes/routes)})}})

(defn start-system! []
  (timbre/set-min-level! :debug)
  (ig/init components))
