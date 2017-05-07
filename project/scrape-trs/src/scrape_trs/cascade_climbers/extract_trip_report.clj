(ns scrape-trs.cascade-climbers.extract-trip-report
  (:require [scrape-trs.protocol
             :refer [map->TripReport trip-report-date-format]])
  (:import java.text.SimpleDateFormat))

(def date-in-format (SimpleDateFormat. "MM/dd/yyyy"))

(defn extract-trip-report
  "Given a trip report page from cascade climbers (as a string), return a
  TripReport record parsed from the page."
  [trip-report-page]
  (let [body (reaver/extract (reaver/parse trip-report-page)
                             []
                             "#body0" reaver/text)
        title (second (re-find #"^Trip: (.*) Date:" body))
        date-string (second (re-find #"Date: ([\d/]+) Trip Report:" body))
        date (.parse date-in-format date-string)
        text (second (re-find #"Trip Report: (.*)" body))]
    (map->TripReport {:title title
                      :date (.format trip-report-date-format date)
                      :text text})))
