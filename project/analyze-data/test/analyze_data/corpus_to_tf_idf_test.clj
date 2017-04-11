(ns analyze-data.corpus-to-tf-idf-test
  (:require [clojure.test :refer [deftest is testing]]
            [analyze-data.corpus-to-tf-idf :as test-ns]))

(deftest corpus-to-tf-idf-data-test
  (let [corpus [{"item-name" "foo", "item-text" "my dog rover"}
                {"item-name" "bar", "item-text" "my dog spot"}]
        result (test-ns/corpus-to-tf-idf-data corpus)]
    (is (map? result) "returns a map")
    (is (contains? result :idf) "contains :idf key")
    (is (contains? result :tf-idf) "contains :tf-idf key")
    (testing ":tf-idf"
      (let [tf-idf (:tf-idf result)
            first-row (first tf-idf)
            second-row (second tf-idf)
            third-row (nth tf-idf 2)]
        (is (= 4 (count tf-idf))
            "contains same number of rows as input plus two header rows")
        (is (= ["dog" "rover" "spot"] first-row)
            "first row is header row of sorted terms")
        (is (= ["foo" "bar"] second-row)
            "second row is header row of item names")
        (is (every? #(= java.lang.Double (type %)) third-row)
            "elements of third row are tf-idf values")))))
