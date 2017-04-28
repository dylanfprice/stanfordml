(ns analyze-data.corpus-to-dataset-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [analyze-data.corpus-to-dataset :as test-ns]
            [analyze-data.test-fixtures :refer [use-vectorz]]))

(use-fixtures :once use-vectorz)

(deftest corpus-to-dataset-test
  (let [corpus [{"document-label" "dog", "document-text" "my dog rover"}
                {"document-label" "dog", "document-text" "my dog spot"}
                {"document-label" "cat", "document-text" "my cat rover"}]
        dataset-types [:tf-idf]]
    (testing "for all dataset-types,"
      (doseq [dataset-type dataset-types]
        (let [result (test-ns/corpus-to-dataset dataset-type corpus)]
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
          (is (map? (:extra result)) "extra is a map"))))
    (testing ":tf-idf dataset-type"
      (let [result (test-ns/corpus-to-dataset :tf-idf corpus)
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
                                    vals)))))))))
