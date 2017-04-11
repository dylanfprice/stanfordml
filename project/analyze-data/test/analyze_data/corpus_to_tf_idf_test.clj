(ns analyze-data.corpus-to-tf-idf-test
  (:require [clojure.test :refer [deftest is testing]]
            [analyze-data.corpus-to-tf-idf :as test-ns]))

(deftest corpus-to-tf-idf-data-test
  (let [corpus [{"item-name" "foo", "item-text" "my dog rover"}
                {"item-name" "bar", "item-text" "my dog spot"}]
        double? #(= java.lang.Double (type %))
        result (test-ns/corpus-to-tf-idf-data corpus)]
    (is (sequential? result) "returns a sequence")
    (is (= 5 (count result))
        "contains same number of rows as input plus three header rows")
    (testing "first row is map from term to inverse document frequency"
      (is (= ["dog" "rover" "spot"] (keys (nth result 0))))
      (is (every? double? (vals (nth result 0)))))
    (is (= ["dog" "rover" "spot"] (nth result 1))
        "second row contains sorted terms")
    (is (= ["foo" "bar"] (nth result 2))
        "third row contains item names")
    (is (every? double? (nth result 3))
        "elements of fourth row are tf-idf values")))
