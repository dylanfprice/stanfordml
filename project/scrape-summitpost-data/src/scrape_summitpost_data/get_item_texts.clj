(ns scrape-summitpost-data.get-item-texts
  (:require [reaver]
            [scrape-summitpost-data.extract :refer [extract-all-pager-links
                                                    extract-result-links
                                                    extract-item-name
                                                    extract-item-text]]))

(def base-url "http://www.summitpost.org")

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
  "Given a link to a search results page from summitpost, GET every search
  results page in the pagination (or just the page if no pagination) and
  return them as a lazy sequence." 
  [link]
  (->> (or (get-pager-links link) [link])
       (map (partial str base-url))
       (map slurp)))

(defn- get-urls
  "Given a link to a search results page from summitpost, return a sequence of
  fully qualified urls to all of the search result items. This function
  performs multiple GET requests."
  [link]
  (->> link
       (get-pages) 
       (map reaver/parse)
       (map extract-result-links)
       (apply concat)
       (map (partial str base-url))))

(defn get-item-texts
  "Given a link to a search results page from summitpost (i.e. relative to the
  summitpost domain), GET all search result items (iterating through
  pagination) and return a sequence of [item-name, item-text] pairs."
  [search-link]
  (->> (get-urls search-link)
       (map #(vector 
               (extract-item-name base-url %) 
               (->> % slurp reaver/parse extract-item-text)))))
