(ns scrape-summitpost-data.extract
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

(defn extract-item-name
  "Given the base url of summitpost a url to an item (such as a mountain),
  return the name of the item."
  [base-url url]
  (nth (re-find (re-pattern (str base-url "/([^/]+)/.*")) url)
       1))

(defn extract-item-text
  "Given a Jsoup document containing an item page from summitpost, return the
  text of the main article."
  [page]
  (reaver/extract page [] "article" reaver/text))
