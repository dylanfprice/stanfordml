(ns analyze-data.dataset.create.core-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [analyze-data.dataset.create.core :as test-ns]
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
                                           :classes
                                           :extra])
            "returned map contains all specced keys")
        (is (= dataset-type (:type result)))
        (is (= (count corpus) (first (m/shape (:X result))))
            "X has # documents rows")
        (is (= (count corpus) (count (:y result)))
            "y has an entry for each document")
        (is (= [1 1 0] (:y result))
            "y contains label indexes for documents")
        (is (= ["cat" "dog"] (:classes result))
            "classes contains sorted distinct labels")
        (is (map? (:extra result)) "extra is a map")))))
