(ns analyze-data.find-knn-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is use-fixtures]]
            [analyze-data.find-knn :as test-ns]))

(defn use-vectorz-fixture
  [f]
  (let [default-implementation (m/current-implementation)]
    (m/set-current-implementation :vectorz)
    (f)
    (m/set-current-implementation default-implementation)))

(use-fixtures :once use-vectorz-fixture)

(deftest knn-named-result-test
  (is (= ["foo" 5]
         (test-ns/knn-named-result ["foo"] [0 5]))
      "transforms index into name"))

(deftest document-to-vector-test
  (let [all-terms ["bar" "foo"]
        idf {"bar" 1, "foo" 2}
        document-to-vector (partial test-ns/document-to-vector
                                    all-terms
                                    idf)]
    (is (= [0.0 0.0]
           (vec (document-to-vector "")))
        "empty document returns 0 for all terms")
    (is (= [1.0 0.0]
           (vec (document-to-vector "bar")))
        "document with a term returns a tf-idf value for that term")))

(deftest find-knn-test
  (let [tf-idf-data {:all-terms ["bar" "foo"]
                     :idf {"bar" 1, "foo" 1}
                     :document-names ["1" "2" "3"]
                     :data [[1 1] [1 0.5] [1 0]]}]
    (is (= ["1" "2" "3"]
           (map first (test-ns/find-knn tf-idf-data "foo bar")))
        "returns 3 nearest neighbors in order")))
