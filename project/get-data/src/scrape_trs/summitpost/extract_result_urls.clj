(ns scrape-trs.summitpost.extract-result-urls
  (:require [reaver]
            [scrape-trs.ensure-sequence :refer [ensure-sequence]]))

(defn extract-result-urls
  "Given a string containing a search results page from summitpost, return a
  sequence of urls representing the results on that page."
  [base-url page]
  (map (partial str base-url)
    (ensure-sequence
      (reaver/extract
        (reaver/parse page)
        []
        ".srch_results .srch_results_lft + .srch_results_rht > a"
        (reaver/attr :href)))))
