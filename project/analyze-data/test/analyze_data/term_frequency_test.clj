(ns analyze-data.term-frequency-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.term-frequency :as test-ns]))

(deftest term-frequency-test
  (is (= {}
         (test-ns/term-frequency []))
      "returns empty map for empty words")
  (is (= {"a" 1}
         (test-ns/term-frequency ["a"]))
      "counts a single word")
  (is (= {"a" 2}
         (test-ns/term-frequency ["a" "a"]))
      "counts a word that appears twice")
  (is (= {"a" 1 "b" 2}
         (test-ns/term-frequency ["b" "a" "b"]))
      "counts multiple words"))

(deftest double-normalized-term-frequency-test
  (is (= {"a" 0.75 "b" 1.0}
         (test-ns/double-normalized-term-frequency ["b" "a" "b"]))
      "computes normalized frequencies"))
