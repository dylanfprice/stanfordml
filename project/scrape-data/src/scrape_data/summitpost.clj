(ns scrape-data.summitpost
  (:require [reaver]))

(def base-url "http://www.summitpost.org")

(defn- extract-search-result-links 
  "Given a Jsoup document containing a search results page from summitpost, 
  return a sequence of links representing the results on that page."
  [page]
  (reaver/extract 
    page
    []
    ".srch_results .srch_results_lft + .srch_results_rht > a" (attr :href)))

(defn- extract-search-result-pager-links
  "Given a Jsoup document containing a search results page from summitpost,
  return a set of links representing other pages of search results from the 
  results pagination."
  [page]
  (let [pager-links (reaver/extract page [] ".pagertext" (attr :href))]
    (->> pager-links
         (remove nil?)
         (map (partial re-find #".*page=\d+"))
         (remove nil?)
         (set))))

(defn- all-search-result-pager-links 
  "Given a link to a paginated search results page,
  GET the page and return a sequence of links to all search results pages."
  [link]
  (let [seed-url (str base-url link)
        page (reaver/parse (slurp seed-url))
        other-links (extract-search-result-pager-links page)]
    (conj other-links link)))

(defn- all-search-result-pages 
  "Given a link to a paginated search results page,
  GET every search results page in the pagination and return them as a lazy 
  sequence."
  [link]
  (->> link
       (all-search-result-pager-links)
       (map (partial str base-url))
       (map slurp)))

(defn- all-search-result-links
  "Given a link to a paginated search results page, return a sequence of
  links to all of the search result items. This function performs multiple
  GET requests."
  [link]
  (->> link
       (all-search-result-pages) 
       (map reaver/parse)
       (map extract-search-result-links)
       (apply concat)))
