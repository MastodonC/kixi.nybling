(ns kixi.nybling-test
  (:require [clojure.test :refer :all]
            [kixi.nybling :refer :all]
            [cheshire.core :as json]
            [taoensso.nippy :as nippy]))

(deftest edn-test
  (testing "EDN string to JSON string"
    (is (= "{\"foo\":\"bar\"}"
           (edn-str-to-json-str "{:foo \"bar\"}"))))
  (testing "EDN string to JSON string - complex"
    (is (= "{\"foo\":1,\"bar\":[1,2,3],\"baz\":{\"a\":\"a\",\"b\":[\"b\",1,2,\"b2\"]}}"
           (edn-str-to-json-str "{:foo 1 :bar [1 2 3] :baz {:a \"a\" :b [\"b\" 1 2 \"b2\"]}}")))))

(deftest transit-test
  (testing "Simples"
    (is (= "[\"^ \",\"~:foo\",\"bar\"]"
           (clj->transit {:foo "bar"}))))
  (testing "Pattern"
    (is (= "[\"^ \",\"~:foo\",[\"~#pattern\",\"bar\"]]"
           (clj->transit {:foo #"bar"}))))
  (testing "Exception"
    (is (string? (clj->transit {:foo (new Exception "msg")}))))
  (testing "ExceptionInfo"
    (is (string? (clj->transit {:foo (ex-info "msg" {:some "data"})})))))
