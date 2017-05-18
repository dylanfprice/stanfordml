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

(deftest terms-with-frequency-less-than
  (is (= ["b"]
         (test-ns/terms-with-frequency-less-than 2 {"a" 2 "b" 1}))
      "returns terms whose frequency is less than 2")
  (is (= []
         (test-ns/terms-with-frequency-less-than 0 {"a" 2 "b" 1}))
      "returns empty list when all terms appear more than n times"))

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
      "handles multiple terms")
  (is (= {"b" 0.0}
         (test-ns/inverse-document-frequency [["a" "b"] ["b"]]
                                             :df-threshold 2))
      (str "when :df-threshold is 2, removes terms that occur in less than "
           "2 documents")))
