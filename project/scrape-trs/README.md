A library for scraping trip report data from various websites.

````clojure
(require '[scrape-trs :refer [list-supported-urls
                              get-trip-reports]])

(list-supported-urls)

(let [url "http://www.summitpost.org/some/tr/search/page"
      page (slurp url)]
  (get-trip-reports url page))
````
