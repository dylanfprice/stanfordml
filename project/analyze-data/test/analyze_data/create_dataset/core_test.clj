(ns analyze-data.create-dataset.core-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [analyze-data.create-dataset.core :as test-ns]
            [analyze-data.test-fixtures :refer [use-vectorz]]))

(use-fixtures :once use-vectorz)

(deftest create-dataset-test
  (let [corpus [{"document-label" "dog", "document-text" "my dog rover"}
                {"document-label" "dog", "document-text" "my dog spot"}
                {"document-label" "cat", "document-text" "my cat rover"}]
        dataset-types [:tf-idf]]
    (doseq [dataset-type dataset-types]
      (let [result (test-ns/create-dataset dataset-type corpus)]
        (is (map? result) "returns a map")
        (is (every? #(contains? result %) [:type
                                           :X
                                           :y
                                           :features
                                           :labels
                                           :extra])
            "returned map contains all specced keys")
        (is (= dataset-type (:type result)))
        (is (= (count corpus) (first (m/shape (:X result))))
            "X has # documents rows")
        (is (= (count corpus) (count (:y result)))
            "y has an entry for each document")
        (is (= [1 1 0] (:y result))
            "y contains label indexes for documents")
        (is (= ["cat" "dog"] (:labels result))
            "labels contains sorted distinct labels")
        (is (map? (:extra result)) "extra is a map")))))
