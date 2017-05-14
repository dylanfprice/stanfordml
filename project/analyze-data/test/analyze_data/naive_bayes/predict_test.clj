(ns analyze-data.naive-bayes.predict-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is]]
            [analyze-data.naive-bayes.predict :as test-ns]))

(deftest predict-test
  (let [parameters {:log-phi (m/log [[1/2 1/4] [1/2 3/4]])
                    :log-phi-y (m/log [1/3 2/3])}
        x [0 1]
        prediction (test-ns/predict parameters x)
        expected-class 1
        expected-prob (float 6/7)]
    (is (= expected-class (first prediction)))
    (is (= (format "%.2f" expected-prob)
           (format "%.2f" (second prediction))))
    (is (= [nil nil]
           (test-ns/predict parameters x, :threshold expected-prob))
        (str "returns [nil nil] when prob of most likely label does not "
             "exceed :threshold"))))
