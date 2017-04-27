(ns analyze-data.evaluate-model
  (:require [clojure.java.io :as io]
            [analyze-data.csv-to-map :refer [csv-to-map]]
            [analyze-data.find-knn :refer [find-knn]]
            [analyze-data.serialize :refer [read-object]]))

(defn get-predictions
  "Make a prediction for each row of test-data using given model.

  tf-idf-model: map produced by corpus-to-tf-idf-model
  test-data: sequence of maps with 'document-label' and 'document-text' keys

  Return a sequence of the form:
  [[test-label [predicted-label distance]]
    ...]"
  [tf-idf-model test-data]
  (let [test-corpus (map #(get % "document-text") test-data)
        test-labels (map #(get % "document-label") test-data)
        predictions (map (partial find-knn tf-idf-model) test-corpus)]
    (map #(vector %1 (first %2)) test-labels predictions)))

(defn- correct-prediction?
  "Given a prediction of the form [test-label [predicted-label distance]],
  return (= test-label predicted-label)."
  [prediction]
  (let [[test-label [predicted-label distance]] prediction]
    (= test-label predicted-label)))

(defn evaluate-model
  "Make a prediction for each row of test-data using given model and return (#
  correct predictions / # total predictions).

  tf-idf-model: map produced by corpus-to-tf-idf-model
  test-data: sequence of maps with 'document-label' and 'document-text' keys"
  [tf-idf-model test-data]
  (let [predictions (get-predictions tf-idf-model test-data)
        correct-predictions (filter correct-prediction? predictions)]
    (float (/ (count correct-predictions) (count predictions)))))

(defn evaluate-model-file
  "Like evaluate-model, but take file path arguments for both model and test
  data.

  tf-idf-model-file: path to a file containing serialized tf-idf-model
  test-file: path to a csv with 'document-label' and 'document-text' headers"
  [tf-idf-model-file test-file]
  (with-open [in (io/reader test-file)]
    (let [model (read-object tf-idf-model-file)
          test-data (csv-to-map in)]
      (evaluate-model model test-data))))
