; Taken from analyze-data.csv-to-map
; if duplicated again, pull out into its own project
(ns create-corpus.csv-to-maps
  (:require [clojure.data.csv :as csv]))

(defn csv-to-maps
  "Given an io/reader of a csv file, return a lazy sequence of maps from csv
  header to data."
  [reader]
  (let [csv-file (csv/read-csv reader)
        header-row (first csv-file)
        data (rest csv-file)]
    (map (partial zipmap header-row) data)))
