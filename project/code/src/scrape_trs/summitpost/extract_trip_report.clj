(ns scrape-trs.summitpost.extract-trip-report
  (:require [clojure.string :as string]
            [scrape-trs.protocol
             :refer [map->TripReport trip-report-date-format]]
            [reaver])
  (:import java.text.SimpleDateFormat))

(defn- extract-item-title
  "Given Jsoup of an item page from summitpost, return the title of the
  page."
  [jsoup]
  (reaver/extract jsoup nil "h1.adventure-title" reaver/text))

(def date-in-format (SimpleDateFormat. "MMM d, yyyy"))

(defn- extract-item-date
  "Given Jsoup of an item page from summitpost, return the trip date 
  in the scrape-trs.protocol/trip-report-date-format."
  [jsoup]
  (let [selector "table.object-properties-table th:contains(Date Climbed/Hiked) + td"
        date-string (reaver/extract jsoup nil selector reaver/text)
        date-string (when (not (string/blank? date-string)) date-string)]
    (some->> date-string
             (.parse date-in-format)
             (.format trip-report-date-format))))

(defn- extract-item-text
  "Given Jsoup of an item page from summitpost, return the text of the main
  article."
  [jsoup]
  (reaver/extract jsoup [] ".full-content" reaver/text))

(defn extract-trip-report
  "Given a trip report page from summitpost (as a string), return a TripReport
  record parsed from the page."
  [trip-report-url trip-report-page]
  (let [jsoup (reaver/parse trip-report-page)]
    (map->TripReport
     {:url trip-report-url
      :title (extract-item-title jsoup)
      :date (extract-item-date jsoup)
      :text (extract-item-text jsoup)})))
