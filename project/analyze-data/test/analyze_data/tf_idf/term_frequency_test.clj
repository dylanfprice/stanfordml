(ns analyze-data.tf-idf.term-frequency-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.tf-idf.term-frequency :as test-ns]))

(deftest term-frequency-test
  (is (= {}
         (test-ns/term-frequency []))
      "returns empty map for empty terms")
  (is (= {"a" 1}
         (test-ns/term-frequency ["a"]))
      "counts a single term")
  (is (= {"a" 2}
         (test-ns/term-frequency ["a" "a"]))
      "counts a term that appears twice")
  (is (= {"a" 1 "b" 2}
         (test-ns/term-frequency ["b" "a" "b"]))
      "counts multiple terms"))

(deftest double-normalized-term-frequency-test
  (is (= {}
         (test-ns/double-normalized-term-frequency []))
      "returns empty map for empty terms")
  (is (= {"a" 0.75 "b" 1.0}
         (test-ns/double-normalized-term-frequency ["b" "a" "b"]))
      "computes normalized frequencies"))
