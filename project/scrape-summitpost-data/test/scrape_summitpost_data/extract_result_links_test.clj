(ns scrape-summitpost-data.extract-result-links-test
  (:require [clojure.string :as string]
            [clojure.test :refer [deftest is]]
            [scrape-summitpost-data.extract-result-links :as test-ns]))

(defn- make-test-page
  [url]
  (let [template "<table class='srch_results'>
                    <tbody>
                    <tr>
                      <td class='srch_results_lft'></td>
                      <td class='srch_results_rht'>
                        <a href='{}'></a>
                      </td>
                    </tr>
                    </tbody>
                  </table>"]
    (string/replace template "{}" url)))

(deftest extract-result-links-test
  (let [page (make-test-page "/test-item-name/12345")]
    (is (= ["/test-item-name/12345"]
           (test-ns/extract-result-links page))
        "extracts link to item from search results table")))
