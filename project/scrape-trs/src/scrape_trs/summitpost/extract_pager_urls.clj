(ns scrape-trs.summitpost.extract-pager-urls
  (:require [reaver]
            [scrape-trs.ensure-sequence :refer [ensure-sequence]]))

(defn extract-pager-links
  "Given a string containing a search results page from summitpost,
  return a sequence of pager links with page=\\d+ query params."
  [page]
  (->> (reaver/extract (reaver/parse page) [] ".pagertext" (reaver/attr :href))
       (ensure-sequence)
       (remove nil?)
       (map (partial re-find #".*page=\d+"))
       (remove nil?)))

(defn- extract-last-page
  "Given a sequence of links with a page=\\d+ query param, return the highest
  value of the param found in the links."
  [links]
  (->> links
       (map (partial re-find #"page=(\d+)"))
       (map #(nth % 1))
       (map #(Integer/parseInt %))
       (apply max)))

(defn- extract-all-pager-urls
  "Given a string containing a search results page from summitpost, return a
  sequence of urls to all pages of search results. If there is no pagination,
  return nil."
  [base-url page]
  (when-let [pager-links (-> page extract-pager-links not-empty)]
    (let [last-page (extract-last-page pager-links)
          template (first pager-links)
          page-numbers (range 1 (+ 1 last-page))
          make-pager-link #(clojure.string/replace
                             template
                             #"page=\d+"
                             (str "page=" %))
          all-pager-links (map make-pager-link page-numbers)]
      (map (partial str base-url) all-pager-links))))
