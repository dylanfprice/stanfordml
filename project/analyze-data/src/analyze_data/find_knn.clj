(ns analyze-data.find-knn
  (:require [clojure.core.matrix :as m]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [analyze-data.knn.core :refer [cosine-similarity knn]]
            [analyze-data.tf-idf.core :refer [tf-idf-document to-terms]]
            [analyze-data.gzip :refer [gzip-reader]])
  (:import java.io.PushbackReader))

(defn read-edn-stream
  "Return a lazy sequence of items parsed from a stream of edn objects. reader
  must be a java.io.PushbackReader or some derivee. Since it is lazy, the
  stream must remain open while new items are being requested."
  [reader]
  (lazy-seq
    (when-let [line (edn/read {:eof nil} reader)]
      (cons line (read-edn-stream reader)))))

(defn read-tf-idf-data
  "TODO"
  [file-path]
  (with-open [file-reader (-> file-path
                              gzip-reader
                              PushbackReader.)]
    (let [tf-idf-stream (read-edn-stream file-reader)]
      {:idf (nth tf-idf-stream 0)
       :all-terms (nth tf-idf-stream 1)
       :item-names (nth tf-idf-stream 2)
       :data (m/sparse-matrix (nthrest tf-idf-stream 3))})))

(defn knn-named-result
  "TODO"
  [item-names result]
  (let [index (first result)
        distance (second result)]
    [(nth item-names index) distance]))

(defn find-knn
  "TODO"
  [tf-idf-data query-document]
  (let [{:keys [idf all-terms item-names data]} tf-idf-data
        query-vector (->> query-document
                          (to-terms)
                          (tf-idf-document all-terms idf)
                          (m/sparse-array))
        nearest-neighbors (knn data
                               query-vector
                               :distance-fn cosine-similarity)]
    (map (partial knn-named-result item-names) nearest-neighbors)))
