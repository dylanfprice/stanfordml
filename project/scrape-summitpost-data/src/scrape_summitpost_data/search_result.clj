(ns scrape-summitpost-data.search-result
  (:require [clojure.java.io :as io]
            [reaver]))

(def base-url "http://www.summitpost.org")

(defn- ensure-sequence
  "If arg is sequential?, return arg. Otherwise return a one-element list
  containing arg."
  [arg]
  (if (sequential? arg) arg (list arg)))

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
  return a sequence of links to all pages of search results."
  [page]
  (let [pager-links (extract-pager-links page)
        last-page (extract-last-page pager-links)
        template (first pager-links)]
    (->> (range 1 (+ 1 last-page))
         (map #(clojure.string/replace 
                 template 
                 #"page=\d+" 
                 (str "page=" %))))))

(defn- get-pager-links 
  "Given a link to a paginated search results page from summitpost, GET the
  page and return a sequence of links to all search results pages."
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
  (->> link
       (get-pager-links)
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

(defn- extract-item-name
  "Given a url to an item on summitpost (such as a mountain), return the name
  of the item."
  [url]
  (nth (re-find (re-pattern (str base-url "/([^/]+)/.*")) url)
       1))

(defn- save-summitpost-item!
  "Given a directory to store files in and a url to a summitpost item,
  GET the url and save to a file."
  [data-dir item-url]
  (let [item-name (extract-item-name item-url)
        file-name (str item-name ".html")
        file-contents (slurp item-url)]
    (spit (str data-dir "/" file-name) file-contents)))

(defn save-summitpost-search-results! 
  "GET all search result items found at `search-link` (iterating through
  pagination) and save them to files in `data-dir`. Creates `data-dir` if
  necessary."
  [data-dir search-link]
  (.mkdir (io/file data-dir))
  (dorun (map (partial save-summitpost-item! data-dir) (get-urls search-link))))
