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

(deftest calc-normalized-term-frequency-test
  (is (= 1.0
         (test-ns/calc-normalized-term-frequency 5 5))
      "term that appears the most number of times is normalize to 1")
  (is (= 0.75
         (test-ns/calc-normalized-term-frequency 5 10))
      "term that appears half as much as max is normalize to .75")
  (is (= 0.5
         (test-ns/calc-normalized-term-frequency 0 10))
      "term that never appears normalized to 0.5"))

(deftest normalized-term-frequency-test
  (is (= {}
         (test-ns/normalized-term-frequency []))
      "returns empty map for empty terms")
  (is (= {"a" 0.75 "b" 1.0}
         (test-ns/normalized-term-frequency ["b" "a" "b"]))
      "computes normalized frequencies"))
