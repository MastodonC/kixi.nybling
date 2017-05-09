(ns kixi.nybling
  (:require [clojure.edn :as edn]
            [cheshire.core :as json]
            [cheshire.generate :refer [add-encoder encode-map]]
            [taoensso.nippy :as nippy]
            [kixi.log.timbre.appenders.logstash :refer [exception->map]])
  (:import (com.fasterxml.jackson.core JsonGenerator JsonGenerationException))

  (:gen-class
   :name kixi.nybling
   :methods [#^{:static true} [ednStringToJsonString [java.lang.String] java.lang.String]
             #^{:static true} [nippyByteArrayToJsonString [bytes] java.lang.String]]))

(add-encoder java.lang.Throwable
             (fn encode-throwable
               [ex jg]
               (encode-map (exception->map ex) jg)))

(add-encoder java.lang.Exception
             (fn encode-exception
               [ex jg]
               (encode-map (exception->map ex) jg)))

(add-encoder clojure.lang.ExceptionInfo
             (fn encode-exception-info
               [exi jg]
               (.writeString jg (str exi))))

(add-encoder java.util.regex.Pattern
             (fn encode-regex
               [rp jg]
               (.writeString jg (str "##" rp))))

(defn edn-str-to-json-str
  "I take an EDN string and convert it to a JSON string"
  [e]
  ((comp json/generate-string edn/read-string) e))

(defn -ednStringToJsonString
  [e]
  (edn-str-to-json-str e))

(defn nippy-byte-array-to-json-str
  "I take a Nippy byte-array and convert it to a JSON string"
  [e]
  ((comp json/generate-string nippy/thaw) e))

(defn -nippyByteArrayToJsonString
  [e]
  (nippy-byte-array-to-json-str e))
