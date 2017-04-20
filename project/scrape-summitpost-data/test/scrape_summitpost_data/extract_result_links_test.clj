(ns scrape-summitpost-data.extract-result-links-test
  (:require [clojure.string :as string]
            [clojure.test :refer [deftest is]]
            [scrape-summitpost-data.extract-result-links :as test-ns]))

(defn- make-test-page
  [links]
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
        rows (mapv #(string/replace row-template "{}" %) links)
        page (conj (cons header rows) footer)]
    (apply str page)))

(deftest extract-result-links-test
  (let [links ["/test1"]]
    (is (= links
           (test-ns/extract-result-links (make-test-page links)))
        "extracts single link from search results table"))
  (let [links ["/test1" "/test2" "/test3"]]
    (is (= links
           (test-ns/extract-result-links (make-test-page links)))
        "extracts multiple links from search results table")))

(deftest extract-all-result-links-test
  (let [all-links [["/test1"]
                  ["/foo1"]]]
    (is (= ["/test1" "/foo1"]
           (test-ns/extract-all-result-links (map make-test-page all-links)))
        "extracts and flattens links from multiple pages")))
