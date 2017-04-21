(ns analyze-data.evaluate-model
  (:require [clojure.java.io :as io]
            [analyze-data.csv-to-map :refer [csv-to-map]]
            [analyze-data.find-knn :refer [find-knn]]
            [analyze-data.serialize :refer [read-object]]))

(defn evaluate-model
  "Make a prediction for each row of test-data using given model and return (#
  correct predictions / # rows of test data).

  tf-idf-model: map produced by corpus-to-tf-idf-model
  test-data: sequence of maps with 'document-name' and 'document-text' keys"
  [tf-idf-model test-data]
  (let [test-corpus (map #(get % "document-text") test-data)
        test-labels (map #(get % "document-name") test-data)
        predictions (map (partial find-knn tf-idf-model) test-corpus)
        prediction-labels (map #(first (first %)) predictions)
        correct-predictions (->> prediction-labels
                                 (map #(vector %1 %2) test-labels)
                                 (filter (partial apply =)))]
    (float (/ (count correct-predictions) (count test-labels)))))

(defn evaluate-model-file
  "Like evaluate-model, but take file path arguments for both model and test
  data."
  [tf-idf-model-file test-file]
  (with-open [in (io/reader test-file)]
    (let [model (read-object tf-idf-model-file)
          test-data (csv-to-map in)]
      (evaluate-model tf-idf-model test-data))))
