(ns analyze-data.corpus-to-tf-idf
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [analyze-data.gzip :refer [gzip-writer]]
            [analyze-data.tf-idf.core :refer [to-terms tf-idf]])
  (:import java.util.zip.GZIPOutputStream))

(defn csv-to-map
  "Given an io/reader of a csv file, return a lazy sequence of maps from csv
  header to data."
  [reader]
  (let [csv-file (csv/read-csv reader)
        header-row (first csv-file)
        data (rest csv-file)]
    (map (partial zipmap header-row) data)))

(defn write-sequence!
  "Write each item in sequence s as a line of edn to file-path. Uses doseq
  such that if s is lazy only a small number of items in s will reside in
  memory at a time.

  There is one option
    :compress? (default false) if true, compress output with gzip"
  [s file-path & options]
  (let [{:keys [compress?] :or {compress? false}} options]
    (with-open [out (if compress?
                      (gzip-writer file-path)
                      (io/writer file-path))]
      (binding [*out* out]
        (doseq [line s] (prn line))))))

(defn corpus-to-tf-idf-data
  "Transform a corpus of documents into a sequence of tf-idf vectors.

  corpus: a sequence of maps containing keys 'item-name' and 'item-text',
          where 'item-name' is a unique identifier and 'item-text' is a
          document

  Return a map of the following form:
  {:idf    {term1 value
            term2 value
            ...}
   :tf-idf [[term1             term2             ...]
            [item1-name        item2-name        ...]
            [item1-term1-value item1-term2-value ...]
            [item2-term1-value item2-term2-value ...]
            ...]}

  Note that :tf-idf is a lazy sequence."
  [corpus]
  (let [document-names (map #(% "item-name") corpus)
        document-texts (map #(% "item-text") corpus)
        tf-idf-corpus (tf-idf (map to-terms document-texts))
        all-terms (:all-terms tf-idf-corpus)
        data (:tf-idf tf-idf-corpus)]
        {:idf (:idf tf-idf-corpus)
         :tf-idf (->> data (cons document-names) (cons all-terms))}))

(defn csv-corpus-to-tf-idf-data!
  "Transform a csv containing a corpus of documents into a file of tf-idf
  vectors and a file of idf values.

  in-path: path to a csv file. It should contain the headers 'item-name' and
           'item-text', with values as described in corpus-to-tf-idf-data.
  idf-path: path to write an edn map from term to idf value.
  tf-idf-path: path where the tf-idf vectors will be written as a gzipped
               file. Each line of the output file will be an edn list, and
               each will correspond to an entry from the :tf-idf key in the
               return value of corpus-to-tf-idf-data."
  [in-path idf-path tf-idf-path]
  (with-open [in (io/reader in-path)
              idf-out (io/writer idf-path)]
    (let [corpus (csv-to-map in)
          tf-idf-data (corpus-to-tf-idf-data corpus)]
      (binding [*out* idf-out] (prn (:idf tf-idf-data)))
      (write-sequence! (:tf-idf tf-idf-data) tf-idf-path, :compress? true))))
