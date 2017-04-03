(ns kixi.nybling-test
  (:require [clojure.test :refer :all]
            [kixi.nybling :refer :all]
            [cheshire.core :as json]
            [taoensso.nippy :as nippy]))

(def cleaned-stress-data
  (dissoc nippy/stress-data
          :exception
          :ex-info
          :regex
          :throwable))

(deftest edn-test
  (testing "EDN string to JSON string"
    (is (= "{\"foo\":\"bar\"}"
           (edn-str-to-json-str "{:foo \"bar\"}"))))
  (testing "EDN string to JSON string - complex"
    (is (= "{\"foo\":1,\"bar\":[1,2,3],\"baz\":{\"a\":\"a\",\"b\":[\"b\",1,2,\"b2\"]}}"
           (edn-str-to-json-str "{:foo 1 :bar [1 2 3] :baz {:a \"a\" :b [\"b\" 1 2 \"b2\"]}}")))))

(deftest nippy-test
  (testing "Nippy byte array to JSON string"
    (is (= "{\"foo\":\"bar\"}"
           (nippy-byte-array-to-json-str (nippy/freeze {:foo "bar"}))))
    (is (= (json/generate-string cleaned-stress-data)
           (nippy-byte-array-to-json-str (nippy/freeze cleaned-stress-data))))))
