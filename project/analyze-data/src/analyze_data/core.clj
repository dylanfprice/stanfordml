(ns analyze-data.core
  (:require [analyze-data.tf-idf.core :refer [to-terms tf-idf]]
            [analyze-data.tf-idf.)

; (with-open [in-file (io/reader "../scrape-summitpost-data/data.csv")]
;   (def row (apply zipmap (take 2 (csv/read-csv in-file)))))

(defn get-tf-idf-corpus
  [documents]
  (let [term-corpus (map to-terms documents)]
    (tf-idf term-corpus)))
