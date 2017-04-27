(ns analyze-data.corpus-to-tf-idf
  (:require [clojure.core.matrix :as m]
            [clojure.java.io :as io]
            [analyze-data.create-sparse-matrix :refer [create-sparse-matrix]]
            [analyze-data.csv-to-map :refer [csv-to-map]]
            [analyze-data.serialize :refer [write-object!]]
            [analyze-data.tf-idf.core :refer [to-terms tf-idf]]))

(defn corpus-to-tf-idf-data
  "Transform a corpus of documents into a map containing a matrix of tf-idf
  values and other associated data. All data will be non-lazy so as to be
  suitable for serialization.

  corpus: a sequence of maps containing keys 'document-label' and
          'document-text'

  Return a map of the following form:
  {:all-terms  [term1 term2 ...]
   :all-labels [label1 label2 ...]
   :idf        {term1 value
                term2 value
                ...}
   :labels [doc1-label-index doc2-label-index ...]
   :tf-idf     [[doc1-term1-value doc1-term2-value ...]
                [doc2-term1-value doc2-term2-value ...]
                ...]}

  :all-terms is a sorted sequence of all terms found in corpus.
  :all-labels is a sorted sequence of all labels in corpus.
  :idf is a map from term to its inverse document frequency.
  :labels is a core.matrix vector of indexes into :all-labels. The first
          entry will represent the label of the first document in :tf-idf,
          and so on.
  :tf-idf is a core.matrix sparse matrix containing the tf-idf values. Each
          row corresponds to a document and each column to a term."
  [corpus]
  (let [document-labels (mapv #(% "document-label") corpus)
        document-texts (map #(% "document-text") corpus)
        all-labels (vec (sort (distinct document-labels)))
        lookup-label-index (reduce-kv (fn [m k v] (assoc m v k))
                                      {}
                                      all-labels)
        labels (mapv (partial lookup-label-index) document-labels)
        tf-idf-data (tf-idf (map to-terms document-texts))]
    (assoc tf-idf-data
           :all-labels all-labels
           :labels (m/array labels)
           :tf-idf (create-sparse-matrix (count labels)
                                         (:tf-idf tf-idf-data)))))

(defn csv-corpus-to-tf-idf-data-file!
  "Transform a csv containing a corpus of documents into a file of tf-idf
  data.

  in-path: path to a csv file. It should contain the headers 'document-label'
           and 'document-text', with values as described in
           corpus-to-tf-idf-data.
  tf-idf-path: path where the tf-idf data will be written as a serialized map.
               You may read this data back in using
               analyze-data.serialize/read-object, make sure you use the same
               core.matrix implementation on each end."
  [in-path tf-idf-path]
  (with-open [in (io/reader in-path)]
    (let [corpus (csv-to-map in)
          tf-idf-data (corpus-to-tf-idf-data corpus)]
      (write-object! tf-idf-path tf-idf-data))))
