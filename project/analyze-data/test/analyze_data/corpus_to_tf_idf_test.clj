(ns analyze-data.corpus-to-tf-idf-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [analyze-data.corpus-to-tf-idf :as test-ns]
            [analyze-data.test-fixtures :refer [use-vectorz]]))

(use-fixtures :once use-vectorz)

(deftest corpus-to-tf-idf-data-test
  (let [corpus [{"document-label" "dog", "document-text" "my dog rover"}
                {"document-label" "dog", "document-text" "my dog spot"}
                {"document-label" "cat", "document-text" "my cat rover"}]
        double? #(= java.lang.Double (type %))
        result (test-ns/corpus-to-tf-idf-data corpus)]
    (is (map? result) "returns a map")
    (is (every? #(contains? result %) [:all-terms
                                       :all-labels
                                       :idf
                                       :labels
                                       :tf-idf])
        "contains :all-terms, :all-labels, :idf, :labels, and :tf-idf keys")
    (is (= ["cat" "dog" "rover" "spot"] (:all-terms result))
        ":all-terms contains sorted terms")
    (is (= ["cat" "dog"] (:all-labels result))
        ":all-labels contains distinct sorted labels")
    (testing ":idf is map from term to inverse document frequency"
      (is (= #{"cat" "dog" "rover" "spot"} (-> result :idf keys set)))
      (is (every? double? (vals (:idf result)))))
    (is (= [1 1 0] (:labels result))
        ":labels contains label indexes for documents")
    (is (not (nil? (first (:tf-idf result))))
        ":tf-idf is not nil")
    (is (every? double? (first (:tf-idf result)))
        "elements of :tf-idf rows are tf-idf values")))
