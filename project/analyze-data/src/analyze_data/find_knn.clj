(ns analyze-data.find-knn
  (:require [clojure.core.matrix :as m]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [analyze-data.knn.core :refer [cosine-distance knn]]
            [analyze-data.tf-idf.core :refer [tf-idf-document to-terms]]
            [analyze-data.gzip :refer [gzip-reader]])
  (:import java.io.PushbackReader))

(defn read-edn-stream
  "Return a lazy sequence of (transform-fn item) for each item parsed from a
  stream of edn objects. transform-fn defaults to identity. reader must be a
  java.io.PushbackReader or some derivee. Since it is lazy, the stream must
  remain open while new items are being requested."
  ([reader] (read-edn-stream reader identity))
  ([reader transform-fn]
    (lazy-seq
      (when-let [line (edn/read {:eof nil} reader)]
        (cons (transform-fn line) (read-edn-stream reader transform-fn))))))

(defn read-tf-idf-data
  "Given the path to a file of gzipped tf-idf data as produced by
  analyze-data.corpus-to-tf-idf/csv-corpus-to-tf-idf-data!, read into a map
  with :idf, :all-terms, :item-names, and :data keys."
  [file-path]
  (with-open [file-reader (-> file-path
                              gzip-reader
                              PushbackReader.)]
    {:idf (edn/read file-reader)
     :all-terms (edn/read file-reader)
     :item-names (edn/read file-reader)
     :data (m/sparse-matrix (read-edn-stream file-reader m/sparse-array))}))

(defn knn-named-result
  "Transform [index value] into [item-name value]."
  [item-names result]
  (let [index (first result)
        distance (second result)]
    [(nth item-names index) distance]))

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

  tf-idf-data: map produced by read-tf-idf-data
  query-document: string

  Return sequence of the form
  [[item-name distance]
   [item-name distance]
   [item-name distance]]"
  [tf-idf-data query-document]
  (let [{:keys [idf all-terms item-names data]} tf-idf-data
        query-vector (document-to-vector all-terms idf query-document)
        nearest-neighbors (knn data
                               query-vector
                               :k 3
                               :distance-fn cosine-distance)]
    (map (partial knn-named-result item-names) nearest-neighbors)))
