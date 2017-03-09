(ns scrape-summitpost-data.search-result-test
  (require [clojure.test :refer [deftest is]]
           [scrape-summitpost-data.search-result :as test-ns]))

(deftest extract-last-page-test
  (is (= 1
         (#'test-ns/extract-last-page ["http://example.org?page=1"]))
      "extracts value of page param")
  (is (= 3
         (#'test-ns/extract-last-page ["http://example.org?page=1"
                                       "http://example.org?page=2"
                                       "http://example.org?page=3"]))
      "extracts highest value of page param")
  (is (thrown? java.lang.NumberFormatException
         (#'test-ns/extract-last-page ["http://example.org?page=f"]))
      "fails on a non-integer value of page param"))
