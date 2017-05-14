(ns scrape-trs.summitpost.extract-trip-report
  (:require [scrape-trs.protocol
             :refer [map->TripReport trip-report-date-format]]
            [reaver])
  (:import java.text.SimpleDateFormat))

(defn- extract-item-title
  "Given Jsoup of an item page from summitpost, return the title of the
  page."
  [jsoup]
  (reaver/extract jsoup [] "header.title h1" reaver/text))

(def date-in-format (SimpleDateFormat. "MMM d, yyyy"))

(defn- extract-item-date
  "Given Jsoup of an item page from summitpost, return the created date of
  the page in the scrape-trs.protocol/trip-report-date-format."
  [jsoup]
  (let [text (reaver/extract jsoup
                             []
                             "p:contains(Created/Edited)"
                             reaver/text)
        date-string (second (re-find #"([A-Z][a-z]+ \d\d?, \d{4}) /" text))
        date (.parse date-in-format date-string)]
    (.format trip-report-date-format date)))

(defn- extract-item-text
  "Given Jsoup of an item page from summitpost, return the text of the main
  article."
  [jsoup]
  (reaver/extract jsoup [] "article" reaver/text))

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
