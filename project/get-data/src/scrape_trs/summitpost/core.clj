(ns scrape-trs.summitpost.core
  (:require [scrape-trs.summitpost
             [extract-pager-urls :refer [extract-all-pager-urls]]
             [extract-result-urls :refer [extract-result-urls]]
             [extract-trip-report :refer [extract-trip-report]]])
  (:import scrape_trs.protocol.ScrapeTripReport))

(def base-url "http://www.summitpost.org")

(deftype SPScrapeTripReport []

  ScrapeTripReport

  (extract-pager-urls
    [this list-page]
    (extract-all-pager-urls base-url list-page))

  (extract-trip-report-urls
    [this list-page]
    (extract-result-urls base-url list-page))

  (extract-trip-report
    [this trip-report-url trip-report-page]
    (extract-trip-report trip-report-url trip-report-page)))
