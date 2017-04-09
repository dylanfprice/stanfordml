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
            second-row (second tf-idf)]
        (is (= 3 (count tf-idf))
            "contains same number of rows as input plus one header row")
        (is (= ["item-name" "dog" "dog rover" "dog spot" "rover" "spot"]
               first-row)
            "first row is header row with sorted terms")
        (is (= "foo"
               (first second-row))
            "first element of second row is first item-name")
        (is (every? #(= java.lang.Double (type %)) (rest second-row))
            "rest of elements of second row are doubles (tf-idf values)")))))
