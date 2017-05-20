(ns get-data.create-corpus
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [clojure.string :as string]
            [get-data.csv-to-map :refer [csv-to-map]]))

(def ^:private first-two-sentences #"([^.]+\.){1,2}")

(defn create-corpus
  "Given a sequence of trip reports, transform it into a sequence of maps ready
  to be turned into a dataset.

  trip-reports: a sequence of maps with keys \"label\", \"title\", \"text\"
  labels: a set of labels representing which items from trip-reports to include
          (case-insensitive)

  Return a sequence of maps with keys \"document-label\" and \"document-text\".
  \"document-label\" is lowercased \"label\" from the source data.
  \"document-text\" is \"title\" and the first two sentences of \"text\"
                    joined by a newline."
  [trip-reports labels]
  (for [trip-report trip-reports
        :let [{:strs [label title text]} trip-report
              lower-label (string/lower-case label)]
        :when (labels lower-label)]
    {"document-label" (string/lower-case label)
     "document-text" (str title
                          "\n"
                          (first (re-find first-two-sentences text)))}))

(defn create-corpus!
  "in: path to csv file of trip reports with headers label, title, text
  out: path to file where csv will be written with headers document-label document-text"
  [in out]
  (with-open [reader (io/reader in)
              writer (io/writer out)]
    (let [trip-reports (csv-to-map reader)
          corpus (create-corpus trip-reports)
          data (for [document corpus]
                 (let [{:strs [document-label document-text]} document]
                   [document-label document-text]))]
      (csv/write-csv writer [["document-label" "document-text"]])
      (csv/write-csv writer data))))
