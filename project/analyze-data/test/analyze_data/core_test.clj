(ns analyze-data.core-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.core :as test-ns]))

(deftest corpus-to-tf-idf-data-test
  (let [corpus [{"item-name" "foo" "item-text" "my dog rover"}
                {"item-name" "bar" "item-text" "my dog spot"}]
        return-value (test-ns/corpus-to-tf-idf-data corpus)
        first-row (first return-value)
        second-row (second return-value)]
    (is (sequential? return-value)
        "returns a sequence")
    (is (= 3 (count return-value))
        "contains same number of rows as input plus one header row")
    (is (= ["item-name" "dog" "dog rover" "dog spot" "rover" "spot"]
           first-row)
        "first row is header row with sorted terms")
    (is (= "foo"
           (first second-row))
        "first element of second row is first item-name")
    (is (and (map #(= java.lang.Double (type %)) second-row))
        "rest of elements of second row are doubles (tf-idf values)")))
