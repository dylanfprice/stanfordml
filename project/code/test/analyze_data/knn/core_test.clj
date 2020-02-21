(ns analyze-data.knn.core-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.knn.core :as test-ns]))

(deftest euclidean-distance-test
  (is (= [1.0 1.0]
         (test-ns/euclidean-distance [[0]
                                      [2]]
                                     [1]))
      "calculates distance between each vector in input matrix and z"))

(deftest knn-test
  (let [X [[2]
           [3]
           [4]
           [5]]]
    (is (= [[0 1.0]
            [1 2.0]
            [2 3.0]]
           (test-ns/knn X [1]))
        "returns the top 3 nearest neighbor indexes and their distance")
    (is (= [[0 1.0]]
           (test-ns/knn X [1], :k 1))
        "returns top 1 nearest neighbor index when k is 1")))

(deftest predict-test
  (let [X [[2] [3] [4]]
        y [0 0 1]
        z [1]]
    (is (= [0 2] (test-ns/predict X y z))
        "returns most likely label and number of votes.")
    (is (= [0 1] (test-ns/predict X y z :k 1))
        "number of votes is 1 when :k is restricted to 1")
    (is (= [nil nil] (test-ns/predict X y z, :threshold 3))
        "returns [nil nil] when number of votes does not exceed :threshold")))
