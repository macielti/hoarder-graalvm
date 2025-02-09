(ns hoarder-graalvm.diplomat.http-client
  (:require [http-client-component.core :as component.http-client]
            [schema.core :as s]))

(s/defn hit-callback-url!
  [callback-url :- s/Str
   http-client]
  @(component.http-client/request! {:url         callback-url
                                    :endpoint-id :callback-url
                                    :method      :post} http-client))
