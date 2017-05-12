(ns scrape-trs.summitpost.extract-result-urls-test
  (:require [clojure.string :as string]
            [clojure.test :refer [deftest is]]
            [scrape-trs.summitpost.extract-result-urls :as test-ns]))

(defn- make-test-page
  [urls]
  (let [header "<table class='srch_results'>
                  <tbody>"
        row-template "<tr>
                        <td class='srch_results_lft'></td>
                        <td class='srch_results_rht'>
                          <a href='{}'></a>
                        </td>
                      </tr>"
        footer "  </tbody>
                </table>"
        rows (mapv #(string/replace row-template "{}" %) urls)
        page (conj (cons header rows) footer)]
    (apply str page)))

(deftest extract-result-urls-test
  (let [urls ["/test1"]]
    (is (= ["http://example.org/test1"]
           (test-ns/extract-result-urls "http://example.org"
                                        (make-test-page urls)))
        "extracts single url from search results table"))
  (let [urls ["/test1" "/test2" "/test3"]]
    (is (= ["http://example.org/test1"
            "http://example.org/test2"
            "http://example.org/test3"]
           (test-ns/extract-result-urls "http://example.org"
                                        (make-test-page urls)))
        "extracts multiple urls from search results table")))
