(ns scrape-summitpost-data.extract-result-links-test
  (:require [clojure.test :refer [deftest is]]
            [scrape-summitpost-data.extract-result-links :as test-ns]))

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
