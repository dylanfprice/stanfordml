(ns scrape-summitpost-data.extract
  (:require [reaver]))


(defn- ensure-sequence
  "If arg is sequential?, return arg. Otherwise return a one-element vector
  containing arg."
  [arg]
  (if (sequential? arg) arg [arg]))

(defn- extract-pager-links
  "Given a Jsoup document containing a search results page from summitpost,
  return a sequence of pager links with page=\\d+ query params."
  [page]
  (->> (reaver/extract page [] ".pagertext" (reaver/attr :href))
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
  "Given a Jsoup document containing a search results page from summitpost,
  return a sequence of links to all pages of search results. If there is no
  pagination, return nil."
  [page]
  (when-let [pager-links (not-empty (extract-pager-links page))]
    (let [last-page (extract-last-page pager-links)
          template (first pager-links)]
      (->> (range 1 (+ 1 last-page))
           (map #(clojure.string/replace 
                   template 
                   #"page=\d+" 
                   (str "page=" %)))))))

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
