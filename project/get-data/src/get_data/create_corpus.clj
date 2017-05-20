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

  Return a sequence of maps with keys \"document-label\" and \"document-text\"
  where \"document-label\" is the same as \"label\" in the source data, and
  \"document-text\" is \"title\" concatenated with the first two sentences of
  \"text\". Filter out trip reports for which \"label\" is blank."
  [trip-reports]
  (for [trip-report trip-reports
        :let [{:strs [label title text]} trip-report]
        :when (not (string/blank? label))]
    {"document-label" label
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