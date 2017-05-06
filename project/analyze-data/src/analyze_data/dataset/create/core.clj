(ns analyze-data.dataset.create.core
  (:require [clojure.java.io :as io]
            [analyze-data.dataset.create [tf-idf :as tf-idf]]
            [analyze-data.csv-to-map :refer [csv-to-map]]
            [analyze-data.serialize :refer [write-object!]]))

(defmulti create-dataset
  "Transform a corpus of documents into a dataset.

  dataset-type: specifies the type of features to extract to make the
                dataset.  Currently the only type is :tf-idf.
  corpus: a sequence of maps containing keys 'document-label' and
          'document-text'
  options:
    :term-types (default [:words]) list of term types to extract from documents
                valid term-types are :words, :bigrams, :trigrams

  Return a dataset, which is a map with the following keys:
  :type      the type of the dataset
  :X         a core.matrix matrix of features where each row represents a
             document and each column a feature
  :y         a vector of indexes into :classes. The first entry will represent
             the label of the first document in :X, and so on.
  :features  a vector of names for each feature column
  :classes   a vector of sorted distinct labels in the corpus
  :extra     a map for type-specific extra data"
  (fn [dataset-type corpus & options] dataset-type))

(defmethod create-dataset :tf-idf [dataset-type corpus & options]
  (apply tf-idf/create-dataset dataset-type corpus options))

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
       sure you use the same core.matrix implementation on each end.
  options: same as for create-dataset"
  [dataset-type in out & options]
  (with-open [reader (io/reader in)]
    (let [corpus (csv-to-map reader)
          dataset (apply create-dataset dataset-type corpus options)]
      (write-object! out dataset))))
