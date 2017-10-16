(ns kixi.nybling-test
  (:require [clojure.test :refer :all]
            [baldr.core :as baldr :refer [baldr-writer]]
            [kixi.nybling :refer :all]
            [cheshire.core :as json]
            [taoensso.nippy :as nippy]
            [clojure.java.io :as io])
  (:import [java.io ByteArrayInputStream]))

(defn hydrate
  [baldrised-data]
  (->> baldrised-data
       .array
       (new ByteArrayInputStream)
       baldr/baldr-seq
       (map nippy/thaw)))

(deftest baldrisation-test
  (let [data {:event {:data "please"}
              :partition-key "whatever"}
        baldrick (payload->baldr-byte-buffer data)]
    (is (= data
           (first (hydrate baldrick))))))
