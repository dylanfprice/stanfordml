(ns analyze-data.create-sparse-matrix
  (:require [clojure.core.matrix :as m]))

(defn create-sparse-matrix
  "Given
  num-rows: number of rows in the resultant matrix
  data: lazy sequence of rows of data

  Return core.matrix sparse matrix containing data."
  [num-rows data]
  (let [shape (cons num-rows (m/shape (first data)))
        matrix (m/new-sparse-array shape)
        indexed-data (map-indexed #(vector %1 %2) data)]
    (doseq [[index row] indexed-data]
      (m/set-row! matrix index row))
    matrix))
