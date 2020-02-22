(ns analyze-data.evaluate-model
  (:require [clojure.core.matrix :as m]
            [analyze-data.dataset.derive
             :refer [partition-dataset-k-fold split-dataset]]
            [analyze-data.predict :refer [predict]]
            [analyze-data.train-model :refer [train-model]]))

(defn get-predictions
  "Make a prediction for each row of test-dataset using given model.

  Arguments are as for evaluate-model.

  Return a sequence of the form:
  [[label predicted-label]
    ...]"
  [model test-dataset & options]
  (let [{:keys [X y classes]} test-dataset
        labels (map classes y)
        predicted-labels (map #(apply predict model % options) X)]
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
  "Make a prediction for each row of test-dataset using given model and
  return a confusion matrix.

  model: model produced by analyze-data.naive-bayes.train/train
  test-dataset: dataset produced by
                analyze-data.dataset.create.core/create-dataset
                Must have same :type :features and :classes as the dataset
                the model was trained with.
  options: passed through to analyze-data.predict/predict

  Return a confusion matrix as a nested map. For example, if the classes of
  the model are :a and :b, returns
  {:a {:a <number of documents classified as :a that were predicted :a>
       :b <number of documents classified as :a that were predicted :b>}
   :b {:a <number of documents classified as :b that were predicted :a>
       :b <number of documents classified as :b that were predicted :b>}}"
  [model test-dataset & options]
  (let [predictions (apply get-predictions model test-dataset options)
        classes (-> model :dataset :classes)
        inc-prediction-count (fn [m [label predicted-label]]
                               (update-in m [label predicted-label] inc))]
    (reduce inc-prediction-count
            (empty-confusion-matrix classes)
            predictions)))

(defn train-and-evaluate
  "Train a model using train-dataset and evaluate it using test-dataset.
  Return a confusion matrix (see evaluate-model for details).

  options are passed through to analyze-data.predict/predict"
  [model-type train-dataset test-dataset & options]
  (let [model (train-model model-type train-dataset)]
    (apply evaluate-model model test-dataset options)))

(defn k-fold-cross-validation
  "Randomly partition dataset into k partitions, ensuring at least two samples
  in each partition (note this may result in less than k partitions). Then
  perform the following algorithm:
    for each partition i
      train model using everything but partition i as dataset
      evaluate model using partition i as test dataset
    return sum of all k confusion matrices

  options are as for train-and-evaluate"
  [k model-type dataset & options]
  (let [partitions (partition-dataset-k-fold dataset k)
        do-train-evaluate (fn [[train-dataset test-dataset]]
                            (apply train-and-evaluate
                                   model-type
                                   train-dataset
                                   test-dataset
                                   options))
        confusion-matrices (pmap do-train-evaluate partitions)]
    (apply merge-with (partial merge-with +) confusion-matrices)))

(defn progressively-train-and-evaluate
  "Evaluate a model against both training and test sets as the size of both
  grows. Starting with 1/k sized sets, work up to approximately 1/2 via
  increments of the numerator. Return a sequence of maps with keys
  :num-samples, :test-confusion-matrix, :train-confusion-matrix."
  [k model-type dataset & options]
  (let [half-k-plus-one (-> k (/ 2) Math/floor int (+ 1))
        fractions (map #(/ % k) (range 1 half-k-plus-one))]
    (for [fraction fractions]
      (let [splits [fraction fraction]
            splits (if (< (apply + splits) 1)
                     (conj splits (- 1 (* 2 fraction)))
                     splits)
            [train-dataset test-dataset _] (split-dataset splits dataset)
            num-samples (-> train-dataset :X m/shape first)
            model (train-model model-type train-dataset)
            test-confusion-matrix
            (apply evaluate-model model test-dataset options)
            train-confusion-matrix
            (apply evaluate-model model train-dataset options)]
        {:num-samples num-samples
         :test-confusion-matrix test-confusion-matrix
         :train-confusion-matrix train-confusion-matrix}))))
