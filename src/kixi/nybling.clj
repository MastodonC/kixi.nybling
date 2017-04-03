(ns kixi.nybling
  (:require [clojure.edn :as edn]
            [cheshire.core :as json])
  (:gen-class
   :name kixi.nybling
   :methods [#^{:static true} [edn-str-to-json-str [java.lang.String] java.lang.String]]))

(defn edn-str-to-json-str
  "I take an EDN string and convert it to a JSON string"
  [e]
  ((comp json/generate-string edn/read-string) e))
