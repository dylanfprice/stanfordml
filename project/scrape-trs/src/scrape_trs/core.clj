(ns scrape-trs.core
  (:require [scrape-trs.cascade-climbers.core :as cc]))

(defn- get-implementation
  [base-url]
  (case base-url
    cc/base-url (cc/->CCScrapeTripReport)))

(defn get-trip-reports
  ""
  [list-page-url]
  (let [base-url (re-find #"https?://.*?/" list-page-url)
        implementation (get-implementation base-url)
        list-page (slurp list-page)
        pager-urls (.extract-pager-urls implementation list-page)
        list-pages (map slurp pager-urls)
        trip-report-urls (mapcat (partial .extract-trip-report-urls
                                          implementation)
                                 list-pages)
        trip-reports (map (partial .extract-trip-report implementation)
                          trip-report-urls
                          (map slurp trip-report-urls))]
    trip-reports))

(defn save-trip-reports!
  ""
  [f trip-reports])
