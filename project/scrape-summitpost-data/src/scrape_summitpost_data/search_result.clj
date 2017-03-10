(ns scrape-summitpost-data.search-result
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [reaver]))

(def base-url "http://www.summitpost.org")

(defn- ensure-sequence
  "If arg is sequential?, return arg. Otherwise return a one-element vector
  containing arg."
  [arg]
  (if (sequential? arg) arg [arg]))

(defn- extract-result-links 
  "Given a Jsoup document containing a search results page from summitpost, 
  return a sequence of links representing the results on that page."
  [page]
  (ensure-sequence
    (reaver/extract
      page
      []
      ".srch_results .srch_results_lft + .srch_results_rht > a"
      (reaver/attr :href))))

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

(defn- extract-all-pager-links
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

(defn- extract-item-name
  "Given a url to an item on summitpost (such as a mountain), return the name
  of the item."
  [url]
  (nth (re-find (re-pattern (str base-url "/([^/]+)/.*")) url)
       1))

(defn- extract-item-text
  "Given a Jsoup document containing an item page from summitpost, return the
  text of the main article."
  [page]
  (reaver/extract page [] "article" reaver/text))

(defn- get-pager-links 
  "Given a link to a paginated search results page from summitpost, GET the
  page and return a sequence of links to all search results pages. If there
  is no pagination, return nil."
  [link]
  (->> link
       (str base-url)
       (slurp)
       (reaver/parse)
       (extract-all-pager-links)))

(defn- get-pages 
  "Given a link to a paginated search results page from summitpost, GET every
  search results page in the pagination and return them as a lazy sequence."
  [link]
  (->> (or (get-pager-links link) [link])
       (map (partial str base-url))
       (map slurp)))

(defn- get-urls
  "Given a link to a paginated search results page from summitpost (i.e.
  relative to the summitpost domain), return a sequence of fully qualified
  urls to all of the search result items. This function performs multiple GET
  requests."
  [link]
  (->> link
       (get-pages) 
       (map reaver/parse)
       (map extract-result-links)
       (apply concat)
       (map (partial str base-url))))

(defn- get-item-texts
  "GET all search result items found at `search-link` (iterating through
  pagination) and return a sequence of [item-name, item-text] pairs."
  [search-link]
  (->> (get-urls search-link)
       (map #(vector 
               (extract-item-name %) 
               (->> % slurp reaver/parse extract-item-text)))))

(defn save-summitpost-search-results!
  "Save a csv of item names and page texts for all search result items found
  at `search-link`."
  [file-name search-link]
  (with-open [out-file (io/writer file-name)]
    (csv/write-csv out-file
                   (cons ["item-name", "item-text"]
                         (get-item-texts search-link)))))
