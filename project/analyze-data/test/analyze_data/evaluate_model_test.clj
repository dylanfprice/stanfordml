(ns analyze-data.evaluate-model-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [analyze-data.evaluate-model :as test-ns]
            [analyze-data.test-fixtures :refer [use-vectorz]]))

(use-fixtures :once use-vectorz)

(def knn-model
  {:type :knn
   :parameters nil
   :dataset {:type :tf-idf
             :X [[1 1] [1 0.5] [1 0] [0.5 1]]
             :y [0 0 1 0]
             :features ["bar" "foo"]
             :classes ["one" "two"]
             :extra {:inverse-document-frequencies
                     {"bar" 1, "foo" 1}}}})

(def test-dataset
  (assoc (:dataset knn-model)
         :X [[0.5 1] [1 2]]
         :y [0 1]))

(deftest get-predictions-test
  (is (= [["one" "one"] ["two" "one"]]
         (test-ns/get-predictions knn-model test-dataset))))

(deftest empty-confusion-matrix-test
  (is (= {:a {:a 0 :b 0} :b {:a 0 :b 0}}
         (#'test-ns/empty-confusion-matrix [:a :b]))))

(deftest evaluate-model-test
  (is (= {"one" {"one" 1, "two" 0}
          "two" {"one" 1, "two" 0}}
         (test-ns/evaluate-model knn-model test-dataset))
      "evaluates a model"))

(deftest train-and-evaluate-test
  (is (= {"one" {"one" 1, "two" 0}
          "two" {"one" 1, "two" 0}}
         (test-ns/train-and-evaluate :knn (:dataset knn-model) test-dataset))
      "trains and evaluates a model"))

(deftest k-fold-cross-validation-test
  (is (= {"one" {"one" 1, "two" 2}
          "two" {"one" 1, "two" 0}}
         (test-ns/k-fold-cross-validation 2 :knn (:dataset knn-model)))
      "trains and evaluates k-fold"))
