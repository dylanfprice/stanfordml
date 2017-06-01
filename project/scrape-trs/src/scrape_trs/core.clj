(ns scrape-trs.core
  (:require [clojure.string :as string]
            [scrape-trs.cascade-climbers.core :as cc]
            [scrape-trs.summitpost.core :as sp]))

(def ^:private implementations
  {cc/base-url (cc/->CCScrapeTripReport)
   sp/base-url (sp/->SPScrapeTripReport)})

(defn list-supported-urls
  "Return a sequence of the base urls of all sites with trip report scrapers."
  []
  (keys implementations))

(defn- get-implementation
  "Given a url whose base is in (list-supported-urls), return an
  implementation of scrape-trs.protocol/ScrapeTripReport."
  [url]
  (second (first (filter #(string/starts-with? url (key %)) implementations))))

(defn get-trip-reports
  "
  url: url of list-page or the site it was retrieved from. Must correspond to
       a supported url in (list-supported-urls).
  list-page: page (as a string) that lists trip reports

  Scrape all trip reports from the list-page, paging through pagination if
  necessary, and return a lazy sequence of scrape-trs.protocol/TripReport
  instances."
  [url list-page]
  (let [implementation (get-implementation url)
        pager-urls (.extract-pager-urls implementation list-page)
        list-pages (if pager-urls (map slurp pager-urls) [list-page])
        trip-report-urls (mapcat #(.extract-trip-report-urls implementation %)
                                 list-pages)
        trip-reports (map #(.extract-trip-report implementation %1 %2)
                          trip-report-urls
                          (map slurp trip-report-urls))]
    trip-reports))
