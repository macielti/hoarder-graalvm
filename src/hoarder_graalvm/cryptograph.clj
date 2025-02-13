(ns hoarder-graalvm.cryptograph
  (:require [clojure.java.io :as io]
            [taoensso.nippy :as nippy]))

(defn encrypt-file!
  "Encrypts a binary file (e.g., an image) with a password and saves it to a new file."
  [input-file output-file password]
  (let [data (with-open [in (io/input-stream input-file)]
               (let [buffer (byte-array (.length (io/file input-file)))]
                 (.read in buffer)
                 buffer))
        encrypted-data (nippy/freeze data {:password [:salted password]})]
    (with-open [out (io/output-stream output-file)]
      (.write out encrypted-data))))

(defn decrypt-file!
  "Decrypts a binary file (e.g., an image) with a password and saves it to a new file."
  [input-file output-file password]
  (let [encrypted-data (with-open [in (io/input-stream input-file)]
                         (let [buffer (byte-array (.length (io/file input-file)))]
                           (.read in buffer)
                           buffer))
        decrypted-data (nippy/thaw encrypted-data {:password [:salted password]})]
    (with-open [out (io/output-stream output-file)]
      (.write out decrypted-data))))
