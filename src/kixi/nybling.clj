(ns kixi.nybling
  (:gen-class
   :name kixi.nybling
   :methods [[kinesisEventRecordToJson [com.amazonaws.services.lambda.runtime.events.KinesisEvent$KinesisEventRecord] java.lang.String]
             [kinesisEventRecordToBaldrByteBuffer [com.amazonaws.services.lambda.runtime.events.KinesisEvent$KinesisEventRecord] java.io.ByteBuffer]])
  (:require [baldr.core :as baldr :refer [baldr-writer]]
            [cheshire.core :as json]
            [cheshire.generate :refer [add-encoder encode-map]]
            [kixi.log.timbre.appenders.logstash :refer [exception->map]]
            [taoensso.nippy :as nippy])
  (:import [com.amazonaws.services.lambda.runtime.events KinesisEvent$KinesisEventRecord KinesisEvent$Record]
           com.fasterxml.jackson.core.JsonGenerator
           java.io.OutputStream
           java.nio.ByteBuffer))

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

(defn kinesis-event-record->json
  [^KinesisEvent$KinesisEventRecord kinesis-event-record]
  (let [^KinesisEvent$Record record (.getKinesis kinesis-event-record)
        our-event (nippy/thaw (.array (.getData record)))]
    (json/generate-string our-event)))

(defn -kinesisEventRecordToJson
  [^KinesisEvent$KinesisEventRecord kinesis-event-record]
  (kinesis-event-record->json kinesis-event-record))

(defn- wrap-event-with-meta
  [^KinesisEvent$Record record
   our-event]
  {:event our-event
   :partition-key (.getPartitionKey record)
   :sequence-num (.getSequenceNumber record)})

(def baldr-header-size 8)

(def byte-array-class (Class/forName "[B"))

(defn payload->baldr-byte-buffer
  [payload]
  (let [^bytes nippied-payload (nippy/freeze payload)
        buffer (ByteBuffer/allocate (+ baldr-header-size
                                       (alength nippied-payload)))
        out (proxy [OutputStream] []
              (write
                ([b]
                 (if (instance? byte-array-class b)
                   (.put buffer ^bytes b)
                   (.putInt buffer (int b))))
                ([^bytes b off len]
                 (.put buffer b off len))))]
    ((baldr-writer out) nippied-payload)
    (.rewind buffer)
    buffer))

(defn kinesis-event-record->baldr-byte-buffer
  [^KinesisEvent$KinesisEventRecord kinesis-event-record]
  (let [^KinesisEvent$Record record (.getKinesis kinesis-event-record)
        event-barray (.array (.getData record))
        meta-wrapped-event (wrap-event-with-meta record event-barray)]
    (payload->baldr-byte-buffer meta-wrapped-event)))

(defn -kinesisEventRecordToBaldrByteBuffer
  [^KinesisEvent$KinesisEventRecord kinesis-event-record]
  (kinesis-event-record->baldr-byte-buffer kinesis-event-record))
