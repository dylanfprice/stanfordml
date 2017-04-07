(ns analyze-data.corpus-to-tf-idf
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [analyze-data.tf-idf.core :refer [to-terms tf-idf]]))

(defn csv-to-map
  "Given an io/reader of a csv file, return a lazy sequence of maps from csv
  header to data."
  [reader]
  (let [csv-file (csv/read-csv reader)
        header-row (first csv-file)
        data (rest csv-file)]
    (map #(zipmap header-row %) data)))

(defn write-sequence!
  "Write each item in sequence s as a line of edn to file-path. Uses doseq
  such that if s is lazy only a small number of items in s will reside in
  memory at a time."
  [s file-path]
  (with-open [out (io/writer file-path)]
    (doseq [line s]
      (.write out (prn-str line)))))

(defn corpus-to-tf-idf-data
  "Transform a corpus of documents into a sequence of tf-idf vectors.

  corpus: a sequence of maps containing keys 'item-name' and 'item-text',
          where 'item-name' is a unique identifier and 'item-text' is a
          document

  Return a lazy sequence of the following form:
  [['item-name' term1        term2        ...]
   [item-name1  term1-tf-idf term2-tf-idf ...]
   [item-name2  term1-tf-idf term2-tf-idf ...]
   ...]"
  [corpus]
  (let [document-names (map #(% "item-name") corpus)
        document-texts (map #(% "item-text") corpus)
        tf-idf-corpus (tf-idf (map to-terms document-texts))
        header-row (cons "item-name" (first tf-idf-corpus))
        data (map #(cons %1 %2) document-names (rest tf-idf-corpus))]
        (cons header-row data)))

(defn csv-corpus-to-tf-idf-data!
  "Transform a csv containing a corpus of documents into a file of tf-idf
  vectors.

  in-path: path to a csv file. It should contain the headers 'item-name' and
           'item-text', with values as described in corpus-to-tf-idf-data.
  out-path: path where the tf-idf vectors should be written. Each line of the
            output file will be an edn list, and each will correspond to an
            entry from the return value of corpus-to-tf-idf-data."
  [in-path out-path]
  (with-open [in (io/reader in-path)]
    (let [corpus (csv-to-map in)
          tf-idf-data (corpus-to-tf-idf-data corpus)]
      (write-sequence! tf-idf-data out-path))))
