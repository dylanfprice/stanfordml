(ns analyze-data.corpus-to-tf-idf
  (:require [clojure.core.matrix :as m]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [analyze-data.serialize :refer [write-object!]]
            [analyze-data.tf-idf.core :refer [to-terms tf-idf]]))

(defn csv-to-map
  "Given an io/reader of a csv file, return a lazy sequence of maps from csv
  header to data."
  [reader]
  (let [csv-file (csv/read-csv reader)
        header-row (first csv-file)
        data (rest csv-file)]
    (map (partial zipmap header-row) data)))

(defn corpus-to-tf-idf-data
  "Transform a corpus of documents into a map containing a matrix of tf-idf
  values and other associated data. All data will be non-lazy so as to be
  suitable for serialization.

  corpus: a sequence of maps containing keys 'document-name' and
          'document-text', where 'document-name' is a unique identifier and
          'document-text' is a document

  Return a map of the following form:
  {:all-terms  [term1 term2 ...]
   :idf        {term1 value
                term2 value
                ...}
   :document-names [item1 item2 ...]
   :tf-idf     [[term1-value term2-value ...]
                [term1-value term2-value ...]
                ...]}

  :all-terms is a sorted sequence of all terms found in the corpus.
  :document-names is a sequence of the 'document-name' keys.
  :idf is a map from term to its inverse document frequency.
  :tf-idf is a core.matrix sparse matrix containing the tf-idf values. Each
          row corresponds to a document and each column to a term."
  [corpus]
  (let [document-names (mapv #(% "document-name") corpus)
        document-texts (map #(% "document-text") corpus)
        tf-idf-data (tf-idf (map to-terms document-texts))]
    (assoc tf-idf-data
           :document-names document-names
           :tf-idf (m/sparse-matrix (:tf-idf tf-idf-data)))))

(defn csv-corpus-to-tf-idf-data!
  "Transform a csv containing a corpus of documents into a file of tf-idf
  data.

  in-path: path to a csv file. It should contain the headers 'document-name'
           and 'document-text', with values as described in
           corpus-to-tf-idf-data.
  tf-idf-path: path where the tf-idf data will be written as a serialized
               map. You may read this data back in using
               analyze-data.serialize/read-object, make sure you use the same
               core.matrix implementation on each end."
  [in-path tf-idf-path]
  (with-open [in (io/reader in-path)]
    (let [corpus (csv-to-map in)
          tf-idf-data (corpus-to-tf-idf-data corpus)]
      (write-object! tf-idf-path tf-idf-data))))
