(ns analyze-data.naive-bayes.train-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is use-fixtures]]
            [analyze-data.naive-bayes.train :as test-ns]
            [analyze-data.test-fixtures :refer [use-vectorz]]))

(use-fixtures :once use-vectorz)

(deftest group-indices-by-value-test
  (is (= {}
         (#'test-ns/group-indices-by-value (m/array [])))
      "returns empty map when y is empty")
  (is (= {0.0 [1 3], 1.0 [0 2]}
         (#'test-ns/group-indices-by-value (m/array [1.0 0.0 1.0 0.0])))
      "returns map where ith entry indexes entries in y with value i")
  (is (= [0.0 1.0 2.0]
         (keys (#'test-ns/group-indices-by-value (m/array [2.0 1.0 0.0]))))
      "returns a sorted map"))

(deftest sum-samples-by-label-test
  (is (= (m/matrix [])
         (#'test-ns/sum-samples-by-label [] (sorted-map)))
      "returns empty matrix when design matrix is empty")
  (is (= (m/matrix [])
         (#'test-ns/sum-samples-by-label [[1 2] [3 4]] (sorted-map)))
      "return empty matrix when label-indices is empty")
  (is (= (m/matrix [[1 2] [3 4]])
         (#'test-ns/sum-samples-by-label [[1 2] [3 4]]
                                         (sorted-map 0 [0], 1 [1])))
      "returns matrix equal to X when each row is already labelled by its
       index")
  (is (= (m/matrix [[4 4] [2 2]])
         (#'test-ns/sum-samples-by-label [[1 1] [2 2] [1 1] [2 2]]
                                         (sorted-map 0 [1 3], 1 [0 2])))
      "returns matrix where ith row is sum of samples labelled i"))

(deftest calc-phi-test
  (is (= (m/matrix [[1/2 1/4] [1/2 3/4]])
         (#'test-ns/calc-phi [[1 0] [0 1] [1 1]]
                             (sorted-map 0 [0], 1 [1 2])))
      "returns a k x n matrix where each entry is prob(j|y=i)"))

(deftest calc-phi-y-test
  (let [label-indices (sorted-map 0 [0], 1 [1 2])
        num-samples 3]
  (is (vector? (#'test-ns/calc-phi-y label-indices num-samples))
      "returns a vector")
  (is (= 2
         (count (#'test-ns/calc-phi-y label-indices num-samples)))
      "returns a vector of length equal to number of class labels")
  (is (= [1/3 2/3]
         (#'test-ns/calc-phi-y label-indices num-samples))
      "returns (# docs in class i / total # of docs)")))

(deftest train-test
  (let [X [[1 0] [0 1] [1 1]]
        y [0 1 1]]
    (is (= [:log-phi :log-phi-y] (keys (test-ns/train X y)))
        "returns map with keys :phi and :phi-y")
    (is (= {:log-phi (m/log (m/matrix [[1/2 1/4] [1/2 3/4]]))
            :log-phi-y (m/log [1/3 2/3])}
           (test-ns/train X y))
        "returns parameters phi and phi-y")))
