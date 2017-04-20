(ns scrape-summitpost-data.extract-result-links
  (:require [reaver]
            [scrape-summitpost-data.ensure-sequence
             :refer [ensure-sequence]]))

(defn extract-result-links
  "Given a string containing a search results page from summitpost, return a
  sequence of links representing the results on that page."
  [page]
  (ensure-sequence
    (reaver/extract
      (reaver/parse page)
      []
      ".srch_results .srch_results_lft + .srch_results_rht > a"
      (reaver/attr :href))))

(defn extract-all-result-links
  "Given a sequence of search results pages from summitpost (as strings),
  return a sequence of fully qualified urls to all of the search result
  items."
  [search-pages]
  (->> search-pages
       (map extract-result-links)
       (apply concat)))
