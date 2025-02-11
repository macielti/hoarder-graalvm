(ns hoarder-graalvm.diplomat.http-client
  (:require [cheshire.core :as json]
            [http-client-component.core :as component.http-client]
            [schema.core :as s]))

(s/defn hit-callback-url!
  [callback-url :- s/Str
   http-client]
  @(component.http-client/request! {:url         callback-url
                                    :endpoint-id :callback-url
                                    :method      :post} http-client))

(s/defn fetch-telegram-file-path :- s/Str
  [external-file-id :- s/Str
   telegram-token :- s/Str
   http-client]
  (-> @(component.http-client/request! {:url         (format "https://api.telegram.org/bot%s/getFile?file_id=%s" telegram-token external-file-id)
                                        :method      :get
                                        :endpoint-id :fetch-telegram-file-path
                                        :payload     {:accept :json}} http-client)
      :body
      (json/decode true)
      :result
      :file_path))

(s/defn download-telegram-document!
  [file-path :- s/Str
   telegram-token :- s/Str
   http-client]
  (-> @(component.http-client/request! {:url         (format "https://api.telegram.org/file/bot%s/%s" telegram-token file-path)
                                        :method      :get
                                        :endpoint-id :download-telegram-document
                                        :payload     {:as :stream}} http-client)
      :body))
