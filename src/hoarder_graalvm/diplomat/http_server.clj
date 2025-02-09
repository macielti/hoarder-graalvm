(ns hoarder-graalvm.diplomat.http-server
  (:require [common-clj.traceability.core :as traceability]
            [hoarder-graalvm.diplomat.http-server.file :as diplomat.http-server.file]
            [hoarder-graalvm.interceptors.file :as interceptors.file]
            [hoarder-graalvm.wire.in.file :as wire.in.file]
            [io.pedestal.http.ring-middlewares :as ring-mw]
            [service-component.interceptors :as service.interceptors]))

(def routes [["/api/files"
              :post [traceability/with-correlation-id-http-interceptor
                     (service.interceptors/schema-body-in-interceptor {:file wire.in.file/File})
                     diplomat.http-server.file/create-file!]
              :route-name :create-file]

             ["/api/files/:file-id"
              :get [traceability/with-correlation-id-http-interceptor
                    diplomat.http-server.file/fetch-file]
              :route-name :fetch-file]

             ["/api/files/:file-id/upload"
              :post [interceptors.file/file-existence-check-interceptor
                     interceptors.file/already-uploaded-file-check-interceptor
                     (ring-mw/multipart-params)
                     diplomat.http-server.file/upload-file!] :route-name :upload-file]])
