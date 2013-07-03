(ns stanfordml.test.problemset1
  (:require [clojure.test :refer :all])
  (:require [incanter.core :refer [matrix]])
  (:require [stanfordml.problemset1 :as problemset1])
  (:require [stanfordml.utils :refer [float=]]))
 

(deftest test-h-theta-bnr []
  (is (float= (problemset1/h-theta-bnr
           (matrix 1 2 2)
           (matrix 1 2 1))
         (matrix 8.81e-1 2 1))))

(deftest test-delta-fn []
  (is (= (problemset1/delta-fn
           0.0001
           0.0001
           (matrix 1 2 1)
           (matrix 1 2 2)
           (matrix 1 2 1)
           (matrix 0 2 1))))
         (matrix -1 2 1))

(deftest test-newtons-method []
  (is (= (problemset1/newtons-method  
           (matrix 1 2 2)
           (matrix 4 2 1)
           (fn [X y theta] 1)
           :converge-delta 2)
         (matrix -1 2 1))))

