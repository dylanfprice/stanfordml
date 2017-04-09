(ns analyze-data.corpus-to-tf-idf-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.corpus-to-tf-idf :as test-ns]))

(deftest corpus-to-tf-idf-data-test
  (let [corpus [{"item-name" "foo" "item-text" "my dog rover"}
                {"item-name" "bar" "item-text" "my dog spot"}]
        result (test-ns/corpus-to-tf-idf-data corpus)
        first-row (first result)
        second-row (second result)]
    (is (sequential? result)
        "returns a sequence")
    (is (= 3 (count result))
        "contains same number of rows as input plus one header row")
    (is (= ["item-name" "dog" "dog rover" "dog spot" "rover" "spot"]
           first-row)
        "first row is header row with sorted terms")
    (is (= "foo"
           (first second-row))
        "first element of second row is first item-name")
    (is (every? #(= java.lang.Double (type %)) (rest second-row))
        "rest of elements of second row are doubles (tf-idf values)")))
