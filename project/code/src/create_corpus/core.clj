(ns create-corpus.core
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [clojure.string :as string]
            [create-corpus.csv-to-maps :refer [csv-to-maps]]))

(defn create-corpus
  "Given a sequence of trip reports, transform it into a sequence of maps ready
  to be turned into a dataset.

  trip-reports: a sequence of maps with keys \"label\", \"title\", \"text\"
  labels: a set of labels representing which items from trip-reports to include
          (case-insensitive)

  Return a sequence of maps with keys \"document-label\" and \"document-text\".
  \"document-label\" is lowercased and trimmed \"label\" from the source data.
  \"document-text\" is \"title\" and the first two sentences of \"text\"
                    joined by a newline."
  [trip-reports labels]
  (for [trip-report trip-reports
        :let [{:strs [label title text]} trip-report
              normalized-label (-> label string/lower-case string/trim)]
        :when (labels normalized-label)]
    {"document-label" normalized-label
     "document-text" (str title "\n" text)}))

(defn create-corpus!
  "in: path to csv file of trip reports with headers label, title, text
  out: path to file where csv will be written with headers document-label document-text
  labels: set of labels representing which trip reports to include (case-insensitive)"
  [in out labels]
  (with-open [reader (io/reader in)
              writer (io/writer out)]
    (let [trip-reports (csv-to-maps reader)
          corpus (create-corpus trip-reports labels)
          data (for [document corpus]
                 (let [{:strs [document-label document-text]} document]
                   [document-label document-text]))]
      (csv/write-csv writer [["document-label" "document-text"]])
      (csv/write-csv writer data))))
