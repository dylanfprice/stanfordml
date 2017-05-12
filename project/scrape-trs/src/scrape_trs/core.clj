(ns scrape-trs.core
  (:require [scrape-trs.cascade-climbers.core :as cc]))

(comment
  "TODO:
   - add SPScrapeTripReport
   - upgrade get-trip-reports to return [errors, trip-reports]
     based on try-slurp that returns [error, contents]
   - add docs and tests for get-trip-reports
   - implement save-trip-reports!
   - make scrape-wa-trs project that
       retrieves cc.com tr list pages for each region in WA (POST)
       retrieves sp.com tr list page for WA
       get-trip-reports on each page and concat all together
       save to csv"

  (def cascade-climbers-washington-regions
    #{"Alpine Lakes"
      "Central/Eastern  Washington"
      "Columbia River Gorge"
      "Ice Climbing Forum"
      "Mount Rainier NP"
      "North Cascades"
      "Olympic Peninsula"
      "Rock Climbing Forum"
      "Southern WA Cascades"
      "the *freshiezone*"}))

(defn get-implementation
  [url]
  (let [base-url (re-find #"https?://.*?/" url)]
    (condp = base-url
      cc/base-url (cc/->CCScrapeTripReport))))

(defn get-trip-reports
  ""
  [implementation list-page]
  (let [pager-urls (.extract-pager-urls implementation list-page)
        list-pages (if pager-urls (map slurp pager-urls) [list-page])
        trip-report-urls (mapcat #(.extract-trip-report-urls implementation %)
                                 list-pages)
        trip-reports (map #(.extract-trip-report implementation %1 %2)
                          trip-report-urls
                          (map slurp trip-report-urls))]
    trip-reports))

(defn save-trip-reports!
  ""
  [f trip-reports])
