(ns scrape-trs.cascade-climbers.extract-pager-links
  (:require [reaver]
            [scrape-trs.ensure-sequence :refer [ensure-sequence]]))

(defn- extract-pager-links
  "Given a string containing the trip reports page from cascade climbers,
  return a sequence of pager links."
  [page]
  (let [links (reaver/extract (reaver/parse page)
                              []
                              ".pagination .alt-1 a"
                              (reaver/attr :href))]
    (if (nil? links) [] (ensure-sequence links))))

(defn- extract-last-page
  "Given a sequence of links ending in /page/\\d+, return the highest page
  number found in the links."
  [links]
  (->> links
       (map (partial re-find #"/page/(\d+)"))
       (map #(nth % 1))
       (map #(Integer/parseInt %))
       (apply max)))

(defn extract-all-pager-links
  "Given a string containing a the trip reports forum page from cascade climbers, return a
  sequence of links to all pages of trip reports."
  [page]
  (when-let [pager-links (-> page extract-pager-links not-empty)]
    (let [last-page (extract-last-page pager-links)
          template (first pager-links)
          page-numbers (range 1 (+ 1 last-page))
          make-pager-link #(clojure.string/replace
                             template
                             #"/page/\d+"
                             (str "/page/" %))
          all-pager-links (map make-pager-link page-numbers)]
      all-pager-links)))
