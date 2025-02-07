(ns hoarder-graalvm.diplomat.http-server
  (:require [common-clj.traceability.core :as traceability]
            [hoarder-graalvm.diplomat.http-server.file :as diplomat.http-server.file]
            [hoarder-graalvm.wire.in.file :as wire.in.file]
            [service-component.interceptors :as service.interceptors]))

(def routes [["/api/files"
              :post [traceability/with-correlation-id-http-interceptor
                     (service.interceptors/schema-body-in-interceptor {:file wire.in.file/File})
                     diplomat.http-server.file/create-file!]
              :route-name :create-file]

             ["/api/files/:file-id"
              :get [traceability/with-correlation-id-http-interceptor
                    diplomat.http-server.file/fetch-file]
              :route-name :fetch-file]])
