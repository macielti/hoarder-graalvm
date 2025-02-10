(ns hoarder-graalvm.wire.out.fragment
  (:require [schema.core :as s]))

(s/defschema Fragment
  {:id               s/Str
   :file-id          s/Str
   :external-file-id s/Str
   :index            s/Int
   :hash             s/Str
   :created-at       s/Str})
