(ns scrape-summitpost-data.extract-result-links-test
  (:require [clojure.string :as string]
            [clojure.test :refer [deftest is]]
            [scrape-summitpost-data.extract-result-links :as test-ns]))

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

(deftest extract-result-links-test
  (let [urls ["/test1"]]
    (is (= urls
           (test-ns/extract-result-links (make-test-page urls)))
        "extracts single link from search results table"))
  (let [urls ["/test1" "/test2" "/test3"]]
    (is (= urls
           (test-ns/extract-result-links (make-test-page urls)))
        "extracts multiple links from search results table")))
