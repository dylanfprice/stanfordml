(ns analyze-data.tf-idf.inverse-document-frequency-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.tf-idf.inverse-document-frequency :as test-ns]))

(deftest document-frequency-test
  (is (= {}
         (test-ns/document-frequency []))
      "empty map when no documents")
  (is (= {"a" 1}
         (test-ns/document-frequency [["a" "a"]]))
      "counts single term in single document")
  (is (= {"a" 2}
         (test-ns/document-frequency [["a" "a"] ["a" "a"]]))
      "counts single term in multiple documents")
  (is (= {"a" 2 "b" 1}
         (test-ns/document-frequency [["a" "b"] ["a"]]))
      "counts multiple terms"))

(deftest calc-inverse-document-frequency-test
  (is (= (Math/log 5)
         (test-ns/calc-inverse-document-frequency 5 1))))

(deftest inverse-document-frequency-test
  (is (= {}
         (test-ns/inverse-document-frequency []))
      "empty map when no documents")
  (is (= {"a" (Math/log 1)}
         (test-ns/inverse-document-frequency [["a"]]))
      "log(1) when single document containing term")
  (is (= {"a" (Math/log 2)}
         (test-ns/inverse-document-frequency [["a"] []]))
      "log(2) when two documents and only one contains term")
  (is (= {"a" (Math/log 2) "b" 0.0}
         (test-ns/inverse-document-frequency [["a" "b"] ["b"]]))
      "handles multiple terms"))
