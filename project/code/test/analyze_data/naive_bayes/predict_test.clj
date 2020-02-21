(ns analyze-data.naive-bayes.predict-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is testing]]
            [analyze-data.naive-bayes.predict :as test-ns]))

(deftest predict-test
  (let [parameters {:log-phi (m/log [[1/2 1/4] [1/2 3/4]])
                    :log-phi-y (m/log [1/3 2/3])}
        x [0 1]
        expected-class 1
        expected-prob (float 6/7)]
    (let [prediction (test-ns/predict parameters x)]
      (is (= expected-class (first prediction))
          "predicts the correct class")
      (is (= (format "%.2f" expected-prob)
             (format "%.2f" (second prediction)))
          "calculates the correct probability"))
    (testing ":threshold option"
      (is (= [nil nil]
             (test-ns/predict parameters x, :threshold 0.99))
          "returns [nil nil] when prob does not exceed :threshold"))))
