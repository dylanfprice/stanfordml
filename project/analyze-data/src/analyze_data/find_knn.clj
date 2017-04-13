(ns analyze-data.find-knn
  (:require [clojure.core.matrix :as m]
            [analyze-data.knn.core :refer [cosine-distance knn]]
            [analyze-data.tf-idf.core :refer [tf-idf-document to-terms]]))

(defn knn-named-result
  "Transform [index value] into [document-name value]."
  [document-names result]
  (let [index (first result)
        distance (second result)]
    [(nth document-names index) distance]))

(defn document-to-vector
  "Turn a document into a core.matrix/sparse-array of tf-idf values.

  all-terms: order of features in vector
  idf: map from term to inverse document frequency
  document: string of document text"
  [all-terms idf document]
  (->> document (to-terms) (tf-idf-document all-terms idf) (m/sparse-array)))

(defn find-knn
  "Find 3 nearest neighbors (per cosine-distance) to query-document in
  tf-idf-data.

  tf-idf-data: map produced by corpus-to-tf-idf-data
  query-document: string

  Return sequence of the form
  [[document-name distance]
   [document-name distance]
   [document-name distance]]"
  [tf-idf-data query-document]
  (let [{:keys [all-terms idf document-names tf-idf]} tf-idf-data
        query-vector (document-to-vector all-terms idf query-document)
        nearest-neighbors (knn tf-idf
                               query-vector
                               :k 3
                               :distance-fn cosine-distance)]
    (map (partial knn-named-result document-names) nearest-neighbors)))
