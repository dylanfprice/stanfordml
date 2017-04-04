(ns analyze-data.tf-idf.inverse-document-frequency-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.tf-idf.inverse-document-frequency :as test-ns]))

(deftest document-frequency-test
  (is (= {}
         (test-ns/document-frequency []))
      "empty map when no documents")
  (is (= {"a" 1}
         (test-ns/document-frequency [{"a" 2}]))
      "counts single term in single document")
  (is (= {"a" 2}
         (test-ns/document-frequency [{"a" 2} {"a" 2}]))
      "counts single term in multiple documents")
  (is (= {"a" 2 "b" 1}
         (test-ns/document-frequency [{"a" 2 "b" 2} {"a" 2}]))
      "counts multiple terms"))

(deftest calc-inverse-document-frequency-test
  (is (= (Math/log 5)
         (test-ns/calc-inverse-document-frequency 5 1))))

(deftest inverse-document-frequency-test
  (is (= {}
         (test-ns/inverse-document-frequency [] []))
      "empty map when no documents")
  (is (= {"a" (Math/log 1)}
         (test-ns/inverse-document-frequency ["a"] [{"a" 1}]))
      "log(1) when single document containing term")
  (is (= {"a" (Math/log 2)}
         (test-ns/inverse-document-frequency ["a"] [{"a" 1} {}]))
      "log(2) when two documents and only one contains term")
  (is (= {"a" (Math/log 2) "b" 0.0}
         (test-ns/inverse-document-frequency ["a" "b"]
                                             [{"a" 1 "b" 1} {"b" 1}]))
      "handles multiple terms"))
