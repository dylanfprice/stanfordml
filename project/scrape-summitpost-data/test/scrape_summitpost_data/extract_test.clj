(ns scrape-summitpost-data.extract-test
  (:require [clojure.test :refer [deftest is]]
            [scrape-summitpost-data.extract :as test-ns]))

(deftest extract-item-name-test
  (is (= "test-item-name"
         (test-ns/extract-item-name "http://example.org" "http://example.org/test-item-name/12345"))
      "extracts item name from page"))
