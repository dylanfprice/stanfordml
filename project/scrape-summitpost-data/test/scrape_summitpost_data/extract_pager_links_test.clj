(ns scrape-summitpost-data.extract-pager-links-test
  (:require [clojure.test :refer [deftest is]]
            [scrape-summitpost-data.extract-pager-links :as test-ns]))

(deftest extract-pager-links-test
  (let [page "<td>
              <a href='/test?foo=bar&page=1' class='pagertext'></a>
              </td>"]
    (is (= ["/test?foo=bar&page=1"]
           (#'test-ns/extract-pager-links page))
        "extracts pager link"))
  (let [page "<td>
              <a href='/test?foo=bar&page=1' class='pagertext'></a>
              <a href='/test?foo=bar' class='pagertext'></a>
              </td>"]
    (is (= ["/test?foo=bar&page=1"]
           (#'test-ns/extract-pager-links page))
        "ignores links without page param")))

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

(deftest extract-all-pager-links-test
  (let [page "<td></td>"]
    (is (= nil
           (test-ns/extract-all-pager-links page))
        "returns nil when there are no pager links"))
  (let [page "<td>
              <a href='/test?page=1' class='pagertext'></a>
              <a href='/test?page=2' class='pagertext'></a>
              <a href='/test?page=6' class='pagertext'></a>
              </td>"]
    (is (= ["/test?page=1", "/test?page=2", "/test?page=3"
            "/test?page=4", "/test?page=5", "/test?page=6"]
           (test-ns/extract-all-pager-links page))
        "extracts entire range of links")))
