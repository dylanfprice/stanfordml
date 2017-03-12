(ns scrape-summitpost-data.extract-test
  (:require [clojure.test :refer [deftest is]]
           [scrape-summitpost-data.extract :as test-ns]))

(deftest ensure-sequence-test
  (is (= [1 2 3] (#'test-ns/ensure-sequence [1 2 3]))
      "returns sequence when given a sequence")
  (is (= [1] (#'test-ns/ensure-sequence 1))
      "returns sequence when given an int"))

(deftest extract-pager-links-test
  (let [jsoup-snippet (reaver/parse
                        "<td>
                         <a href='/test?foo=bar&page=1' class='pagertext'></a>
                         </td>")]
    (is (= ["/test?foo=bar&page=1"]
           (#'test-ns/extract-pager-links jsoup-snippet))
        "extracts pager link"))
  (let [jsoup-snippet (reaver/parse
                        "<td>
                         <a href='/test?foo=bar&page=1' class='pagertext'></a>
                         <a href='/test?foo=bar' class='pagertext'></a>
                         </td>")]
    (is (= ["/test?foo=bar&page=1"]
           (#'test-ns/extract-pager-links jsoup-snippet))
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
  (let [jsoup-snippet (reaver/parse "<td></td>")]
    (is (= nil
           (test-ns/extract-all-pager-links jsoup-snippet))
        "returns nil when there are no pager links"))
  (let [jsoup-snippet (reaver/parse
                        "<td>
                         <a href='/test?page=1' class='pagertext'></a>
                         <a href='/test?page=2' class='pagertext'></a>
                         <a href='/test?page=6' class='pagertext'></a>
                         </td>")]
    (is (= ["/test?page=1", "/test?page=2", "/test?page=3"
            "/test?page=4", "/test?page=5", "/test?page=6"]
           (test-ns/extract-all-pager-links jsoup-snippet))
        "extracts entire range of links")))

(deftest extract-result-links-test
  (let [jsoup-snippet (reaver/parse
                        "<table class='srch_results'>
                           <tbody>
                           <tr>
                             <td class='srch_results_lft'></td>
                             <td class='srch_results_rht'>
                               <a href='/test-item-name/12345'></a>
                             </td>
                           </tr>
                           </tbody>
                         </table>")]
    (is (= ["/test-item-name/12345"]
           (test-ns/extract-result-links jsoup-snippet))
        "extracts link to item from search results table")))

(deftest extract-item-name-test
  (is (= "test-item-name"
         (test-ns/extract-item-name "http://example.org" "http://example.org/test-item-name/12345"))
      "extracts item name from page"))
