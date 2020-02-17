(ns scrape-trs.cascade-climbers.extract-trip-report-test
  (:require [clojure.test :refer [deftest is]]
            [scrape-trs.cascade-climbers.extract-trip-report :as test-ns]
            [scrape-trs.protocol :refer [map->TripReport]]))

(def trip-report-page
  "<div id='body0'>
     <span>Trip:</span> Dragontail - Triple Couloirs
     <span>Date:</span> 4/23/2017
     <span>Trip Report:</span> This is a trip report.
   </div>")

(deftest extract-trip-report-test
  (is (= (map->TripReport
           {:url "http://example.com/trip-reports/1234"
            :title "Dragontail - Triple Couloirs"
            :date "2017-04-23"
            :text "This is a trip report."})
         (test-ns/extract-trip-report "http://example.com/trip-reports/1234"
                                      trip-report-page))
      "extracts trip report"))
