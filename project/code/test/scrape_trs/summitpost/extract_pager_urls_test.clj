(ns scrape-trs.summitpost.extract-pager-urls-test
  (:require [clojure.test :refer [deftest is]]
            [scrape-trs.summitpost.extract-pager-urls :as test-ns]))

(deftest extract-pager-links-test
  (let [page "<div class='pager'>
              <a href='/test?foo=bar&page=2' title='Page # 2'>2</a>
              </div>"]
    (is (= ["/test?foo=bar&page=2"]
           (#'test-ns/extract-pager-links page))
        "extracts pager link"))
  (let [page "<div class='pager'>
              <a href='#'></a>
              <a href='/test?foo=bar'></a>
              <a href='/test?foo=bar&page=2' title='Page # 2'>2</a>
              </div>"]
    (is (= ["/test?foo=bar&page=2"]
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

(deftest extract-all-pager-urls-test
  (let [page "<div class='pager'></div>"]
    (is (= nil
           (test-ns/extract-all-pager-urls "http://example.org" page))
        "returns nil when there are no pager urls"))
  (let [page "<div class='pager'>
              <a href='/test?page=2' title='Page # 2'></a>
              <a href='/test?page=3' title='Page # 3'></a>
              <a href='/test?page=6' title='Page # 6'></a>
              </div>"]
    (is (= ["http://example.org/test?page=1"
            "http://example.org/test?page=2"
            "http://example.org/test?page=3"
            "http://example.org/test?page=4"
            "http://example.org/test?page=5"
            "http://example.org/test?page=6"]
           (test-ns/extract-all-pager-urls "http://example.org" page))
        "extracts entire range of urls")))
