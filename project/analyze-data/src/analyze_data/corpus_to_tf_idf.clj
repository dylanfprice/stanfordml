(ns analyze-data.corpus-to-tf-idf
  (:require [clojure.core.matrix :as m]
            [clojure.java.io :as io]
            [analyze-data.csv-to-map :refer [csv-to-map]]
            [analyze-data.serialize :refer [write-object!]]
            [analyze-data.tf-idf.core :refer [to-terms tf-idf]]))

(defn create-sparse-matrix
  "Given
  num-rows: number of rows in the resultant matrix
  data: lazy sequence of rows of data

  Return core.matrix sparse matrix containing data."
  [num-rows data]
  (let [shape (cons num-rows (m/shape (first data)))
        indexed-data (map-indexed #(vector %1 %2) data)
        matrix (m/new-sparse-array shape)]
    (doseq [[index row] indexed-data]
      (m/set-row! matrix index row))
    matrix))

(defn corpus-to-tf-idf-data
  "Transform a corpus of documents into a map containing a matrix of tf-idf
  values and other associated data. All data will be non-lazy so as to be
  suitable for serialization.

  corpus: a sequence of maps containing keys 'document-name' and
          'document-text', where 'document-name' is a unique identifier.

  Return a map of the following form:
  {:all-terms  [term1 term2 ...]
   :idf        {term1 value
                term2 value
                ...}
   :document-names [doc1-name doc2-name ...]
   :tf-idf     [[doc1-term1-value doc1-term2-value ...]
                [doc2-term1-value doc2-term2-value ...]
                ...]}

  :all-terms is a sorted sequence of all terms found in the corpus.
  :document-names is a sequence of the 'document-name' values.
  :idf is a map from term to its inverse document frequency.
  :tf-idf is a core.matrix sparse matrix containing the tf-idf values. Each
          row corresponds to a document and each column to a term."
  [corpus]
  (let [document-names (mapv #(% "document-name") corpus)
        document-texts (map #(% "document-text") corpus)
        tf-idf-data (tf-idf (map to-terms document-texts))]
    (assoc tf-idf-data
           :document-names document-names
           :tf-idf (create-sparse-matrix (count document-names)
                                         (:tf-idf tf-idf-data)))))

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
