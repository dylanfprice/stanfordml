(ns scrape-summitpost-data.extract-from-item
  (:require [reaver]))

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
