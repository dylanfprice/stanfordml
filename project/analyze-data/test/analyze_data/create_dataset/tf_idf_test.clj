(ns analyze-data.create-dataset.tf-idf-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [analyze-data.create-dataset.tf-idf :as test-ns]
            [analyze-data.test-fixtures :refer [use-vectorz]]))

(use-fixtures :once use-vectorz)

(deftest create-dataset-test
  (let [corpus [{"document-label" "dog", "document-text" "my dog rover"}
                {"document-label" "dog", "document-text" "my dog spot"}
                {"document-label" "cat", "document-text" "my cat rover"}]
        result (test-ns/create-dataset :tf-idf corpus)
        double? #(= java.lang.Double (type %))]
    (is (some? (first (:X result))) "X rows are not nil")
    (is (every? double? (first (:X result)))
        "elements of X rows are tf-idf values")
    (is (= ["cat" "dog" "rover" "spot"] (:features result))
        "features contains sorted terms")
    (testing "has :extra map where"
      (testing (str ":inverse-document-frequencies is map from term to "
                    "inverse document frequency")
        (is (= #{"cat" "dog" "rover" "spot"}
               (-> result :extra :inverse-document-frequencies keys set)))
        (is (every? double? (-> result
                                :extra
                                :inverse-document-frequencies
                                vals)))))))

(deftest document-to-vector-test
  (let [dataset {:features ["bar" "foo"]
                 :extra {:inverse-document-frequencies {"bar" 1, "foo" 2}}}]
    (is (= [0.0 0.0]
           (vec (test-ns/document-to-vector dataset "")))
        "empty document returns 0 for all terms")
    (is (= [1.0 0.0]
           (vec (test-ns/document-to-vector dataset "bar")))
        "document with a term returns a tf-idf value for that term")))
