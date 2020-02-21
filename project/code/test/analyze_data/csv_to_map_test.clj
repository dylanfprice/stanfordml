(ns analyze-data.csv-to-map-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.csv-to-map :as test-ns])
  (:import java.io.StringReader))

(deftest csv-to-map-test
  (is (= []
         (test-ns/csv-to-map (StringReader. "")))
      "parses empty file to empty vector")
  (is (= [{"a" "1", "b" "2"} {"a" "3", "b" "4"}]
         (test-ns/csv-to-map (StringReader. "a,b\n1,2\n3,4")))
      "uses first line as header and parses following lines into maps"))
