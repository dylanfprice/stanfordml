(ns analyze-data.corpus-to-tf-idf-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [analyze-data.corpus-to-tf-idf :as test-ns]))

(defn use-vectorz-fixture
  [f]
  (let [default-implementation (m/current-implementation)]
    (m/set-current-implementation :vectorz)
    (f)
    (m/set-current-implementation default-implementation)))

(use-fixtures :once use-vectorz-fixture)

(deftest corpus-to-tf-idf-data-test
  (let [corpus [{"document-name" "foo", "document-text" "my dog rover"}
                {"document-name" "bar", "document-text" "my dog spot"}]
        double? #(= java.lang.Double (type %))
        result (test-ns/corpus-to-tf-idf-data corpus)]
    (is (map? result) "returns a map")
    (is (every? #(contains? result %) [:all-terms
                                       :document-names
                                       :idf
                                       :tf-idf])
        "contains :all-terms, :document-names, :idf, and :tf-idf keys")
    (is (= ["dog" "rover" "spot"] (:all-terms result))
        ":all-terms contains sorted terms")
    (testing ":idf is map from term to inverse document frequency"
      (is (= ["dog" "rover" "spot"] (keys (:idf result))))
      (is (every? double? (vals (:idf result)))))
    (is (= ["foo" "bar"] (:document-names result))
        ":document-names contains item names")
    (is (not (nil? (first (:tf-idf result))))
        ":tf-idf is not nil")
    (is (every? double? (first (:tf-idf result)))
        "elements of :tf-idf rows are tf-idf values")))
