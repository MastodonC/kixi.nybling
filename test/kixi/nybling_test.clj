(ns kixi.nybling-test
  (:require [clojure.test :refer :all]
            [kixi.nybling :refer :all]))

(deftest a-test
  (testing "EDN string to JSON string"
    (is (= "{\"foo\":\"bar\"}"
           (edn-str-to-json-str "{:foo \"bar\"}"))))
  (testing "EDN string to JSON string - complex"
    (is (= "{\"foo\":1,\"bar\":[1,2,3],\"baz\":{\"a\":\"a\",\"b\":[\"b\",1,2,\"b2\"]}}"
           (edn-str-to-json-str "{:foo 1 :bar [1 2 3] :baz {:a \"a\" :b [\"b\" 1 2 \"b2\"]}}")))))
