(ns scrape-trs.cascade-climbers.extract-pager-urls
  (:require [reaver]
            [scrape-trs.ensure-sequence :refer [ensure-sequence]]))

(defn- extract-pager-urls
  "Given a string containing the trip reports page from cascade climbers,
  return a sequence of pager urls."
  [page]
  (let [urls (reaver/extract (reaver/parse page)
                              []
                              ".pagination .alt-1 a"
                              (reaver/attr :href))]
    (if (nil? urls) [] (ensure-sequence urls))))

(defn- extract-last-page
  "Given a sequence of urls ending in /page/\\d+, return the highest page
  number found in the urls"
  [urls]
  (->> urls
       (map (partial re-find #"/page/(\d+)"))
       (map #(nth % 1))
       (map #(Integer/parseInt %))
       (apply max)))

(defn extract-all-pager-urls
  "Given a string containing a the trip reports forum page from cascade
  climbers, return a sequence of urls to all pages of trip reports."
  [page]
  (when-let [pager-urls (-> page extract-pager-urls not-empty)]
    (let [last-page (extract-last-page pager-urls)
          template (first pager-urls)
          page-numbers (range 1 (+ 1 last-page))
          make-pager-link #(clojure.string/replace
                             template
                             #"/page/\d+"
                             (str "/page/" %))
          all-pager-urls (map make-pager-link page-numbers)]
      all-pager-urls)))
