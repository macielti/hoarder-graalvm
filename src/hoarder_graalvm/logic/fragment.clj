(ns hoarder-graalvm.logic.fragment
  (:require [buddy.core.codecs :as buddy-codecs]
            [buddy.core.hash :as hash]
            [hoarder-graalvm.models.fragment :as models.fragment]
            [java-time.api :as jt]
            [schema.core :as s])
  (:import (java.io File)))

;; TODO: Implement unit tests
(s/defn ->fragment :- models.fragment/Fragment
  [external-file-id :- s/Str
   file-id :- s/Uuid
   fragment-index :- s/Int
   file :- File]
  {:fragment/id               (random-uuid)
   :fragment/file-id          file-id
   :fragment/external-file-id external-file-id
   :fragment/index            fragment-index
   :fragment/hash             (-> (hash/md5 file) buddy-codecs/bytes->hex)
   :fragment/created-at       (jt/local-date-time)})

; TODO: Implement unit tests
(s/defn output-fragment-file-path :- s/Str
  [{:fragment/keys [index file-id]} :- models.fragment/Fragment]
  (format "/tmp/%s-download-part-%d" file-id index))
