(ns analyze-data.create-dataset.core
  (:require [clojure.java.io :as io]
            [analyze-data.create-dataset [tf-idf :as tf-idf]]
            [analyze-data.csv-to-map :refer [csv-to-map]]
            [analyze-data.serialize :refer [write-object!]]))

(defmulti create-dataset
  "Transform a corpus of documents into a dataset.

  dataset-type: specifies the type of features to extract to make the
                dataset.  Currently the only type is :tf-idf.
  corpus: a sequence of maps containing keys 'document-label' and
          'document-text'

  Return a dataset, which is a map with the following keys:
  :type      the type of the dataset
  :X         a core.matrix matrix of features where each row represents a
             document and each column a feature
  :y         a vector of indexes into :labels. The first entry will represent
             the label of the first document in :X, and so on.
  :features  a vector of names for each feature column
  :labels    a vector of sorted distinct labels in the corpus
  :extra     a map for type-specific extra data"
  (fn [dataset-type corpus] dataset-type))

(defmethod create-dataset :tf-idf [dataset-type corpus]
  (tf-idf/create-dataset dataset-type corpus))

(defmulti document-to-vector
  "Turn a document into a core.matrix vector with the same type of features
  as dataset."
  (fn [dataset document] (:type dataset)))

(defmethod document-to-vector :tf-idf [dataset document]
  (tf-idf/document-to-vector dataset document))

(defn create-dataset-file!
  "Like create-dataset, but expects a file name for input and output.

  dataset-type: the type of dataset to create
  in: path to a csv file. It should contain the headers 'document-label' and
      'document-text'
  out: path where the dataset will be written as a serialized map.  You may
       read this data back in using analyze-data.serialize/read-object, make
       sure you use the same core.matrix implementation on each end."
  [dataset-type in out]
  (with-open [reader (io/reader in)]
    (let [corpus (csv-to-map reader)
          dataset (create-dataset dataset-type corpus)]
      (write-object! out dataset))))
