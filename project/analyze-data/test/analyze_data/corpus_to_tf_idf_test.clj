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
  (let [corpus [{"item-name" "foo", "item-text" "my dog rover"}
                {"item-name" "bar", "item-text" "my dog spot"}]
        double? #(= java.lang.Double (type %))
        result (test-ns/corpus-to-tf-idf-data corpus)]
    (is (map? result) "returns a map")
    (is (every? #(contains? result %) [:all-terms :item-names :idf :tf-idf])
        "contains :all-terms, :item-names, :idf, and :tf-idf keys")
    (is (= ["dog" "rover" "spot"] (:all-terms result))
        ":all-terms contains sorted terms")
    (testing ":idf is map from term to inverse document frequency"
      (is (= ["dog" "rover" "spot"] (keys (:idf result))))
      (is (every? double? (vals (:idf result)))))
    (is (= ["foo" "bar"] (:item-names result))
        ":item-names contains item names")
    (is (every? double? (first (:tf-idf result)))
        "elements of :tf-idf rows are tf-idf values")))
