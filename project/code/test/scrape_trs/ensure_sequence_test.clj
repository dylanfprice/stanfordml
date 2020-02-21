(ns scrape-trs.ensure-sequence-test
  (:require [clojure.test :refer [deftest is]]
            [scrape-trs.ensure-sequence :as test-ns]))

(deftest ensure-sequence-test
  (is (= [1 2 3] (test-ns/ensure-sequence [1 2 3]))
      "returns sequence when given a sequence")
  (is (= [1] (test-ns/ensure-sequence 1))
      "returns sequence when given an int"))
