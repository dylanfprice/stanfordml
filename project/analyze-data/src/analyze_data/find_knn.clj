(ns analyze-data.find-knn
  (:require [clojure.core.matrix :as m]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [analyze-data.knn.core :refer [cosine-similarity knn]]
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
  "TODO"
  [file-path]
  (with-open [file-reader (-> file-path
                              gzip-reader
                              PushbackReader.)]
    {:idf (edn/read file-reader)
     :all-terms (edn/read file-reader)
     :item-names (edn/read file-reader)
     :data (m/sparse-matrix (read-edn-stream file-reader m/sparse-array))}))

(defn knn-named-result
  "TODO"
  [item-names result]
  (let [index (first result)
        distance (second result)]
    [(nth item-names index) distance]))

(defn document-to-vector
  "TODO"
  [all-terms idf document]
  (->> document (to-terms) (tf-idf-document all-terms idf) (m/sparse-array)))

(defn find-knn
  "TODO"
  [tf-idf-data query-document]
  (let [{:keys [idf all-terms item-names data]} tf-idf-data
        query-vector (document-to-vector all-terms idf query-document)
        nearest-neighbors (knn data
                               query-vector
                               :distance-fn cosine-similarity)]
    (map (partial knn-named-result item-names) nearest-neighbors)))
