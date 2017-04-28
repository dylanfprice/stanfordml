(ns analyze-data.greatest-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.greatest :as test-ns]))

(deftest greatest-test
  (is (= [0 2]
         (test-ns/greatest [0 2] [1 1]))))
