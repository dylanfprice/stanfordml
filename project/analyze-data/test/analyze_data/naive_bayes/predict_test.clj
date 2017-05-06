(ns analyze-data.naive-bayes.predict-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is]]
            [analyze-data.naive-bayes.predict :as test-ns]))

(deftest predict-test
  (let [parameters {:log-phi (m/log [[1/2 1/4] [1/2 3/4]])
                    :log-phi-y (m/log [1/3 2/3])}
        x [0 1]]
    (is (= [1 (+ (Math/log 2/3) (Math/log 3/4))]
           (test-ns/predict parameters x)))))
