(ns analyze-data.predict-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [analyze-data.predict :as test-ns]
            [analyze-data.test-fixtures :refer [use-vectorz]]))

(use-fixtures :once use-vectorz)

(deftest predict-test
  (testing ":knn model"
    (let [model {:type :knn
                 :parameters nil
                 :dataset {:type :tf-idf
                           :X [[1 1] [1 0.5] [1 0]]
                           :y [0 0 1]
                           :features ["bar" "foo"]
                           :labels ["thing-one" "thing-two"]
                           :extra {:inverse-document-frequencies
                                   {"bar" 1, "foo" 1}}}}
          document "foo bar"]
      (is (= "thing-one" (test-ns/predict model document))
          "TODO")))
  (testing ":naive-bayes model"
    (let [model {:type :naive-bayes
                 :parameters {:log-phi (m/log [[1/2 2/3] [1/2 1/3]])
                              :log-phi-y (m/log [2/3 1/3])}
                 :dataset {:type :tf-idf
                           :X [[1 1] [1 0.5] [1 0]]
                           :y [0 0 1]
                           :features ["bar" "foo"]
                           :labels ["thing-one" "thing-two"]
                           :extra {:inverse-document-frequencies
                                   {"bar" 1, "foo" 1}}}}
          document "foo bar"]
      (is (= "thing-one" (test-ns/predict model document))
          "TODO"))))
