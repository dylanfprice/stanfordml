(ns analyze-data.train-model-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [analyze-data.test-fixtures :refer [use-vectorz]]
            [analyze-data.train-model :as test-ns]))

(use-fixtures :once use-vectorz)

(deftest train-model-test
  (let [dataset {:type :test
                 :X (m/matrix [[1 0] [0 1]])
                 :y [0 1]
                 :features ["a" "b"]
                 :labels ["one" "two"]
                 :extra nil}
        model-types [:knn :naive-bayes]]
    (testing "for all model-types,"
      (doseq [model-type model-types]
        (let [result (test-ns/train-model model-type dataset)]
          (is (map? result) "returns a map")
          (is (every? #(contains? result %) [:type
                                             :parameters
                                             :dataset])
              "returned map contains all specced keys")
          (is (= model-type (:type result))
              ":type is the model-type")
          (is (= dataset (:dataset result))
              ":dataset is the given dataset"))))))
