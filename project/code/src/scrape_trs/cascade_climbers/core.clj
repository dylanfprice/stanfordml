(ns scrape-trs.cascade-climbers.core
  (:require [scrape-trs.cascade-climbers
             [extract-pager-urls :refer [extract-all-pager-urls]]
             [extract-tr-urls :refer [extract-tr-urls]]
             [extract-trip-report :refer [extract-trip-report]]])
  (:import scrape_trs.protocol.ScrapeTripReport))

(def base-url "http://cascadeclimbers.com")
(def usage-notes
  (str "TODO"))

(deftype CCScrapeTripReport []

  ScrapeTripReport

  (extract-pager-urls
    [this list-page]
    (extract-all-pager-urls list-page))

  (extract-trip-report-urls
    [this list-page]
    (extract-tr-urls base-url list-page))

  (extract-trip-report
    [this trip-report-url trip-report-page]
    (extract-trip-report trip-report-url trip-report-page)))
