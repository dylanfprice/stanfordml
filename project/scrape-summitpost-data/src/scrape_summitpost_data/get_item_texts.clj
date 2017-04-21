(ns scrape-summitpost-data.get-item-texts
  (:require [clojure.string :as string]
            [scrape-summitpost-data.extract-pager-links
             :refer [extract-all-pager-links]]
            [scrape-summitpost-data.extract-result-links
             :refer [extract-all-result-links]]
            [scrape-summitpost-data.extract-from-item
             :refer [extract-item-name extract-item-text
                     extract-children-links]]))

(def base-url "http://www.summitpost.org")

(defn- get-result-urls
  "Given a link to a paginated search results page from summitpost, GET the
  page and return a sequence of fully qualified urls to all of the search
  results."
  [link]
  (let [page (slurp (str base-url link))
        pager-links (or (extract-all-pager-links page) [link])
        pager-urls (map (partial str base-url) pager-links)
        search-pages (map slurp pager-urls)
        result-links (extract-all-result-links search-pages)]
    (map (partial str base-url) result-links)))

(defn- make-result-entry
  "Return a vector of [result-url, result-name, result-text]."
  [result-url result-text]
  (vector result-url
          (extract-item-name base-url result-url)
          result-text))

(defn get-item-texts
  "Given a link to a search results page from summitpost (i.e. relative to the
  summitpost domain), GET all search result items (iterating through
  pagination if present) and return a sequence of [item-url, item-name,
  item-text] triples."
  [search-link]
  (let [result-urls (get-result-urls search-link)
        result-texts (->> result-urls
                          (map slurp)
                          (map extract-item-text))]
    (map make-result-entry result-urls result-texts)))

(defn- get-child-urls
  [result-page]
  (->> result-page
       extract-children-links
       (map (partial str base-url))))

(defn- get-result-text-and-child-texts
  "Concatenate result page content with contents of pages at child-urls."
  [result-page child-urls]
  (let [result-text (extract-item-text result-page)
        child-pages (map slurp child-urls)
        child-texts (map extract-item-text child-pages)]
    (string/join "\n" (cons result-text child-texts))))

(defn get-item-texts-with-children
  "Like get-item-texts, but also retrieve the content of all pages found
  under the 'Children' heading in the left sidebar of each result page and
  concatenate it on to item-text in the returned vector. If any search result
  ends up being the child of another result, it is removed from the final
  sequence."
  [search-link]
  (let [result-urls (get-result-urls search-link)
        result-pages (map slurp result-urls)
        child-urls (map get-child-urls result-pages)
        all-child-urls (set (apply concat child-urls))
        result-texts (map get-result-text-and-child-texts
                          result-pages
                          child-urls)
        result-entries (map make-result-entry result-urls result-texts)]
    (remove #(all-child-urls (first %)) result-entries)))
