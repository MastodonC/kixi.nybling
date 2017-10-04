(ns kixi.nybling
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [cheshire.generate :refer [add-encoder encode-map]]
            [cognitect.transit :as transit]
            [taoensso.nippy :as nippy]
            [kixi.log.timbre.appenders.logstash :refer [exception->map]])
  (:import (com.fasterxml.jackson.core JsonGenerator JsonGenerationException)
           (com.amazonaws.services.lambda.runtime.events 
            KinesisEvent
            KinesisEvent$KinesisEventRecord
            KinesisEvent$Record)
           (java.io ByteArrayOutputStream)
           (com.fasterxml.jackson.core JsonGenerator))

  (:gen-class
   :name kixi.nybling
   :methods [#^{:static true} [ednStringToJsonString [java.lang.String] java.lang.String]
             #^{:static true} [nippyByteArrayToJsonString [bytes] java.lang.String]
             #^{:static true} [kinesisEventRecordToJsonVersions [com.amazonaws.services.lambda.runtime.events.KinesisEvent$KinesisEventRecord] java.util.List]]))

(def project-definition (some-> (io/resource "project.clj") slurp edn/read-string))

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
               [exi ^JsonGenerator jg]
               (.writeString jg (str exi))))

(add-encoder java.util.regex.Pattern
             (fn encode-regex
               [rp ^JsonGenerator jg]
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

(defn- clj->transit
  ([m]
   (clj->transit m :json))
  ([m t]
   (let [out (ByteArrayOutputStream. 4096)
         writer (transit/writer out t)]
     (transit/write writer m)
     (.toString out))))

(defn- wrap-event-with-meta
  [^KinesisEvent$Record record
   our-event]
  {:event our-event
   :partition-key (.getPartitionKey record)
   :sequence-num (.getSequenceNumber record)
   :dependency-versions {:transit "0.8.300"
                         :cheshire "5.7.0"}})

(defn kinesis-event-record->json-versions
  [^KinesisEvent$KinesisEventRecord kinesis-event-record]
  (let [^KinesisEvent$Record record (.getKinesis kinesis-event-record)
        our-event (nippy/thaw (.array (.getData record)))
        json-for-elastic-search (json/generate-string our-event)
        meta-wrapped-event (wrap-event-with-meta record our-event)
        transit-json-for-S3 (clj->transit meta-wrapped-event)]
    (list json-for-elastic-search
          transit-json-for-S3)))

(defn -kinesisEventRecordToJsonVersions
  [^KinesisEvent$KinesisEventRecord kinesis-event-record]
  (kinesis-event-record->json-versions kinesis-event-record))
