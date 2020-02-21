(ns scrape-trs.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [scrape-trs.cascade-climbers.core :as cc]
            [scrape-trs.summitpost.core :as sp]))

(def implementations
  {cc/base-url
   {:impl (cc/->CCScrapeTripReport)
    :usage-notes cc/usage-notes}
   sp/base-url
   {:impl (sp/->SPScrapeTripReport)
    :usage-notes sp/usage-notes}})

(defn- get-implementation
  "Given a url whose base is in (list-supported-urls), return an
  implementation of scrape-trs.protocol/ScrapeTripReport."
  [url]
  (when (string/includes? url "map_5=1")
    (throw
     (ex-info "Provided url was for map view not list view." {:url url})))
  (->> implementations
       (filter #(string/starts-with? url (key %)))
       first
       val
       :impl))

(defn get-trip-reports
  "
  url: url of list-page. Must correspond to a supported url in implementations.

  Scrape all trip reports from the list-page, paging through pagination if
  necessary, and return a lazy sequence of scrape-trs.protocol/TripReport
  instances."
  [url]
  (let [implementation (get-implementation url)
        list-page (slurp url)
        pager-urls (.extract-pager-urls implementation list-page)
        list-pages (if pager-urls (map slurp pager-urls) [list-page])
        trip-report-urls (mapcat #(.extract-trip-report-urls implementation %)
                                 list-pages)
        trip-reports (map #(.extract-trip-report implementation %1 %2)
                          trip-report-urls
                          (map slurp trip-report-urls))]
    trip-reports))

(defn- maps-to-vectors
  "Given a sequence of maps return them as a sequence of row vectors where the
   first vector is a header row of strings."
  [maps]
  (let [columns (-> maps first keys)
        headers (mapv name columns)
        rows (for [m maps]
               (for [col columns]
                 (col m)))]
    (cons headers rows)))

(defn get-trip-reports!
  "Like get-trip-reports but writes data as csv to given path."
  [url path]
  (let [trip-reports (get-trip-reports url)]
    (with-open [file (io/writer path)]
      (csv/write-csv file (maps-to-vectors trip-reports)))))
