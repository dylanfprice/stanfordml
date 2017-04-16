(ns analyze-data.evaluate-test-data
  (:require [clojure.java.io :as io]
            [analyze-data.csv-to-map :refer [csv-to-map]]
            [analyze-data.find-knn :refer [find-knn]]))

(defn evaluate-test-data
  "test-data: sequence of maps with 'document-name' and 'document-text' keys"
  [tf-idf-data test-data]
  (let [test-corpus (map #(get % "document-text") test-data)
        test-labels (map #(get % "document-name") test-data)
        predictions (map (partial find-knn tf-idf-data) test-corpus)
        prediction-labels (map #(first (first %)) predictions)
        correct-predictions (->> prediction-labels
                                 (map #(vector %1 %2) test-labels)
                                 (filter (partial apply =)))]
    (float (/ (count correct-predictions) (count test-labels)))))

(defn evaluate-test-file
  ""
  [tf-idf-data test-file]
  (with-open [in (io/reader test-file)]
    (let [test-data (csv-to-map in)]
      (evaluate-test-data tf-idf-data test-data))))
