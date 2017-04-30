(ns analyze-data.evaluate-model
  (:require [clojure.java.io :as io]
            [analyze-data.csv-to-map :refer [csv-to-map]]
            [analyze-data.predict :refer [predict-document]]
            [analyze-data.serialize :refer [read-object]]))

(defn get-predictions
  "Make a prediction for each row of test-corpus using given model.

  model: map produced by analyze-data.naive-bayes.train/train
  test-corpus: sequence of maps with 'document-label' and 'document-text' keys
  options: passed through to analyze-data/predict

  Return a sequence of the form:
  [[label predicted-label]
    ...]"
  [model test-corpus & options]
  (let [document-texts (map #(get % "document-text") test-corpus)
        labels (map #(get % "document-label") test-corpus)
        predicted-labels (map #(apply predict-document model % options)
                              document-texts)]
    (map vector labels predicted-labels)))

(defn- empty-confusion-matrix
  "Create confusion matrix with all 0 values.
  For example, if classes = [:a :b], returns
  {:a {:a 0
       :b 0}
   :b {:a 0
       :b 0}}"
  [classes]
  (let [entries (zipmap classes (repeat 0))]
    (zipmap classes (repeat entries))))

(defn evaluate-model
  "Make a prediction for each row of test-corpus using given model and return
  a confusion matrix.

  model: model produced by train-model
  test-corpus: sequence of maps with 'document-label' and 'document-text'
               keys
  options: passed through to analyze-data/predict

  Return a confusion matrix as a nested map. For example, if the classes of the
  model are :a and :b, returns
  {:a {:a <number of documents classified as :a that were predicted :a>
       :b <number of documents classified as :a that were predicted :b>}
   :b {:a <number of documents classified as :b that were predicted :a>
       :b <number of documents classified as :b that were predicted :b>}}

  Note that if any counts are 0 then that entry will not exist."
  [model test-corpus & options]
  (let [predictions (apply get-predictions model test-corpus options)
        classes (-> model :dataset :classes)
        inc-prediction-count (fn [m [label predicted-label]]
                               (update-in m [label predicted-label] inc))]
    (reduce inc-prediction-count
            (empty-confusion-matrix classes)
            predictions)))

(defn evaluate-model-file
  "Like evaluate-model, but take file path arguments for both model and test
  data.

  model-file: path to a file containing serialized model
  test-file: path to a csv with 'document-label' and 'document-text' headers
  options: as for evaluate-model"
  [model-file test-file & options]
  (with-open [in (io/reader test-file)]
    (let [model (read-object model-file)
          test-corpus (csv-to-map in)]
      (apply evaluate-model model test-corpus options))))
