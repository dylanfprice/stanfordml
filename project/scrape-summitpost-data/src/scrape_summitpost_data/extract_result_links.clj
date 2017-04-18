(ns scrape-summitpost-data.extract-result-links
  (:require [reaver]
            [scrape-summitpost-data.ensure-sequence
             :refer [ensure-sequence]]))

(defn extract-result-links
  "Given a Jsoup document containing a search results page from summitpost,
  return a sequence of links representing the results on that page."
  [page]
  (ensure-sequence
    (reaver/extract
      page
      []
      ".srch_results .srch_results_lft + .srch_results_rht > a"
      (reaver/attr :href))))
