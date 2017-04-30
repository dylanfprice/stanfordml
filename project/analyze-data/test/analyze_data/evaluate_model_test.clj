(ns analyze-data.evaluate-model-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [analyze-data.evaluate-model :as test-ns]
            [analyze-data.test-fixtures :refer [use-vectorz]]))

(use-fixtures :once use-vectorz)

(def knn-model
  {:type :knn
   :parameters nil
   :dataset {:type :tf-idf
             :X [[1 1] [1 0.5] [1 0]]
             :y [0 0 1]
             :features ["bar" "foo"]
             :classes ["one" "two"]
             :extra {:inverse-document-frequencies
                     {"bar" 1, "foo" 1}}}})

(def test-corpus
  [{"document-label" "one", "document-text" "foo"}
   {"document-label" "two", "document-text" "bar"}
   {"document-label" "one", "document-text" "foo bar"}])

(deftest get-predictions-test
  (is (= [["one" "one"] ["two" "one"] ["one" "one"]]
         (test-ns/get-predictions knn-model test-corpus))))

(deftest empty-confusion-matrix-test
  (is (= {:a {:a 0 :b 0} :b {:a 0 :b 0}}
         (#'test-ns/empty-confusion-matrix [:a :b]))))

(deftest evaluate-model-test
  (is (= {"one" {"one" 2, "two" 0}
          "two" {"one" 1, "two" 0}}
         (test-ns/evaluate-model knn-model test-corpus))
      "evaluates a model"))
