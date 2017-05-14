(ns scrape-trs.core
  (:require [scrape-trs.cascade-climbers.core :as cc]
            [scrape-trs.summitpost.core :as sp]))

(defn get-implementation
  [url]
  (let [base-url (re-find #"https?://.*?/" url)]
    (condp = base-url
      cc/base-url (cc/->CCScrapeTripReport)
      sp/base-url (sp/->SPScrapeTripReport))))

(defn get-trip-reports
  "implementation: type that implements scrape-trs.protocol/ScrapeTripReport
  list-page: page (as a string) that lists trip reports and corresponds to the given implementation

  Scrape all trip reports from the list-page, paging through pagination if
  necessary, and return a sequence of scrape-trs.protocol/TripReport instances."
  [implementation list-page]
  (let [pager-urls (.extract-pager-urls implementation list-page)
        list-pages (if pager-urls (map slurp pager-urls) [list-page])
        trip-report-urls (mapcat #(.extract-trip-report-urls implementation %)
                                 list-pages)
        trip-reports (map #(.extract-trip-report implementation %1 %2)
                          trip-report-urls
                          (map slurp trip-report-urls))]
    trip-reports))
