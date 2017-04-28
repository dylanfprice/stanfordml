(ns analyze-data.train-model
  (:require [analyze-data.naive-bayes.train :as naive-bayes]
            [analyze-data.serialize :refer [read-object write-object!]]))

(defmulti train-model
  "Train a model on a dataset.

  model-type: one of [:knn :naive-bayes]
  dataset: a dataset (e.g. produced by corpus-to-dataset)

  Return a model, which is a map with the following keys:
  :type       the type of the model
  :parameters the learned parameters of the model
  :dataset    the dataset the model was trained on"
  (fn [model-type dataset] model-type))

(defn train-model-file
  "Like train-model, but accepts a file name for input and output.

  model-type: type of model to create
  in: path to a dataset file
  out: path where the trained model will be written as a serialized map."
  [model-type in out]
  (let [dataset (read-object in)
        model (train-model model-type dataset)]
    (write-object! out model)))

(defmethod train-model :knn [model-type dataset]
  {:type model-type
   :parameters nil
   :dataset dataset})

(defmethod train-model :naive-bayes [model-type dataset]
  (let [{:keys [X y]} dataset]
    {:type model-type
     :parameters (naive-bayes/train X y)
     :dataset dataset}))
