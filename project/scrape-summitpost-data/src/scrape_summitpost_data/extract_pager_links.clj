(ns scrape-summitpost-data.extract-pager-links
  (:require [reaver]
            [scrape-summitpost-data.ensure-sequence
             :refer [ensure-sequence]]))

(defn- extract-pager-links
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

(defn extract-all-pager-links
  "Given a string containing a search results page from summitpost, return a
  sequence of links to all pages of search results. If there is no pagination,
  return nil."
  [page]
  (when-let [pager-links (-> page extract-pager-links not-empty)]
    (let [last-page (extract-last-page pager-links)
          template (first pager-links)]
      (->> (range 1 (+ 1 last-page))
           (map #(clojure.string/replace
                   template
                   #"page=\d+"
                   (str "page=" %)))))))

