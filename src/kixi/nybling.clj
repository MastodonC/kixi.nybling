(ns kixi.nybling
  (:require [clojure.edn :as edn]
            [cheshire.core :as json]
            [taoensso.nippy :as nippy])
  (:gen-class
   :name kixi.nybling
   :methods [#^{:static true} [ednStringToJsonString [java.lang.String] java.lang.String]
             #^{:static true} [nippyByteArrayToJsonString [bytes] java.lang.String]]))

(defn edn-str-to-json-str
  "I take an EDN string and convert it to a JSON string"
  [e]
  ((comp json/generate-string edn/read-string) e))

(defn ednStringToJsonString
  [e]
  (edn-str-to-json-str e))

(defn nippy-byte-array-to-json-str
  "I take a Nippy byte-array and convert it to a JSON string"
  [e]
  ((comp json/generate-string nippy/thaw) e))

(defn nippyByteArrayToJsonString
  [e]
  (nippy-byte-array-to-json-str e))
