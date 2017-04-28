(ns analyze-data.create-dataset
  (:require [clojure.java.io :as io]
            [analyze-data.csv-to-map :refer [csv-to-map]]
            [analyze-data.create-sparse-matrix
             :refer [create-sparse-matrix]]
            [analyze-data.serialize :refer [write-object!]]
            [analyze-data.tf-idf.core :refer [tf-idf to-terms]]))

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

(defn create-dataset-file!
  "Like create-dataset, but expects a file name for input and output.

  in: path to a csv file. It should contain the headers 'document-label' and
      'document-text'
  out: path where the dataset will be written as a serialized map.  You may
       read this data back in using analyze-data.serialize/read-object, make
       sure you use the same core.matrix implementation on each end.
  dataset-type: the type of dataset to create"
  [in out dataset-type]
  (with-open [reader (io/reader in)]
    (let [corpus (csv-to-map reader)
          dataset (create-dataset dataset-type corpus)]
      (write-object! out dataset))))

(defn- get-y-and-labels
  [corpus]
  (let [document-labels (mapv #(% "document-label") corpus)
        labels (vec (sort (distinct document-labels)))
        lookup-label-index (reduce-kv (fn [m k v] (assoc m v k))
                                      {}
                                      labels)
        y (mapv (partial lookup-label-index) document-labels)]
    {:y y :labels labels}))

(defmethod create-dataset :tf-idf [dataset-type corpus]
  (let [{:keys [y labels]} (get-y-and-labels corpus)
        document-texts (map #(% "document-text") corpus)
        {:keys [all-terms tf-idf idf]} (tf-idf (map to-terms document-texts))]
    {:type dataset-type
     :X (create-sparse-matrix (count y) tf-idf)
     :y y
     :features all-terms
     :labels labels
     :extra {:inverse-document-frequencies idf}}))
