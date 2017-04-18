(ns scrape-summitpost-data.get-item-texts
  (:require [scrape-summitpost-data.extract-pager-links
             :refer [extract-all-pager-links]]
            [scrape-summitpost-data.extract-result-links
             :refer [extract-result-links]]
            [scrape-summitpost-data.extract-from-item
             :refer [extract-item-name extract-item-text]]))

(def base-url "http://www.summitpost.org")

(defn- get-pager-urls
  "Given a link to a paginated search results page from summitpost, GET the
  page and return a sequence of fully qualified urls to all search results
  pages."
  [link]
  (let [page (slurp (str base-url link))
        pager-links (or (extract-all-pager-links page) [link])]
    (map (partial str base-url) pager-links)))

(defn- get-result-urls
  "Given a sequence of search results pages from summitpost (as strings),
  return a sequence of fully qualified urls to all of the search result
  items."
  [search-pages]
  (->> search-pages
       (map extract-result-links)
       (apply concat)
       (map (partial str base-url))))

(defn get-item-texts
  "Given a link to a search results page from summitpost (i.e. relative to the
  summitpost domain), GET all search result items (iterating through
  pagination if present) and return a sequence of [item-url, item-name,
  item-text] triples."
  [search-link]
  (let [pager-urls (get-pager-urls link)
        search-pages (map slurp pager-urls)
        result-urls (get-result-urls search-pages)]
  (->> result-urls
       (map #(vector
               %
               (extract-item-name base-url %)
               (->> % slurp extract-item-text)))))
