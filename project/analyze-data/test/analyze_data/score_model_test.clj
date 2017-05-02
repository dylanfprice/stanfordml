(ns analyze-data.score-model-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.score-model :as test-ns]))

(def confusion-matrix
  {:a {:a 10 :b 5  :c 1}
   :b {:a 1  :b 10 :c 5}
   :c {:a 1  :b 0  :c 15}})

(deftest true-positives-test
  (is (= 10 (test-ns/true-positives confusion-matrix :a))
      "calculates true positives for a given class")
  (is (= 35 (test-ns/true-positives confusion-matrix))
      "calculates true positives for all classes"))

(deftest false-positives-test
  (is (= 2 (test-ns/false-positives confusion-matrix :a))
      "calculates false positives for a given class")
  (is (= 13 (test-ns/false-positives confusion-matrix))
      "calculates false positives for all classes"))

(deftest false-negatives-test
  (is (= 6 (test-ns/false-negatives confusion-matrix :a))
      "calculates false negatives for a given class")
  (is (= 13 (test-ns/false-negatives confusion-matrix))
      "calculates false negatives for all classes"))

(deftest true-negatives-test
  (is (= 30 (test-ns/true-negatives confusion-matrix :a))
      "calculates true negatives for a given class")
  (is (= 83 (test-ns/true-negatives confusion-matrix))
      "calculates true negatives for all classes"))

(deftest precision-test
  (is (= 10/12 (test-ns/precision confusion-matrix :a))))

(deftest recall-test
  (is (= 10/16 (test-ns/recall confusion-matrix :a))))

(deftest specificity-test
  (is (= 30/32 (test-ns/specificity confusion-matrix :a))))

(deftest accuracy-test
  (is (= 35/48 (test-ns/accuracy confusion-matrix))))
