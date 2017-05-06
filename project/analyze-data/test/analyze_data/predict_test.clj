(ns analyze-data.predict-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [analyze-data.predict :as test-ns]
            [analyze-data.test-fixtures :refer [use-vectorz]]))

(use-fixtures :once use-vectorz)

(def knn-model
  {:type :knn
   :parameters nil
   :dataset {:type :tf-idf
             :X [[1 1] [1 0.5] [1 0]]
             :y [0 0 1]
             :features ["bar" "foo"]
             :classes ["thing-one" "thing-two"]
             :extra {:inverse-document-frequencies
                     {"bar" 1, "foo" 1}}}})

(def naive-bayes-model
  {:type :naive-bayes
   :parameters {:phi [[1/2 2/3] [1/2 1/3]]
                :phi-y [2/3 1/3]}
   :dataset {:type :tf-idf
             :X [[1 1] [1 0.5] [1 0]]
             :y [0 0 1]
             :features ["bar" "foo"]
             :classes ["thing-one" "thing-two"]
             :extra {:inverse-document-frequencies
                     {"bar" 1, "foo" 1}}}})

(deftest predict-test
  (testing ":knn model"
    (is (= "thing-one" (test-ns/predict knn-model [1 1]))
        (str "predicts thing-one because 2/3 of the nearest docs are "
             "labelled thing-one")))
  (testing ":naive-bayes model"
    (is (= "thing-one" (test-ns/predict naive-bayes-model [1 1])))))

(deftest predict-document-test
  (is (= "thing-one" (test-ns/predict-document knn-model "foo bar"))))
