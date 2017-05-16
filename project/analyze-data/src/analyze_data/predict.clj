(ns analyze-data.predict
  (:require [clojure.core.matrix :as m]
            [analyze-data.dataset.create.core :refer [document-to-vector]]
            [analyze-data.knn.core :as knn]
            [analyze-data.naive-bayes.predict :as naive-bayes]))

(defmulti predict
  "Predict a label for z.

  model: previously trained model
  z: feature vector
  options: specific to the model-type
    :knn see analyze-data.knn.core/predict
    :naive-bayes see analyze-data.naive-bayes.predict/predict

  Return the predicted label."
  (fn [model z & options] (:type model)))

(defn predict-document
  "Predict a label for the given document.

  model: previously trained model
  document: string
  options: see predict

  Return the predicted label."
  [model document & options]
  (let [dataset (:dataset model)
        z (document-to-vector dataset document)]
    (apply predict model z options)))

(defmethod predict :knn [model z & options]
  (let [{{:keys [X y classes]} :dataset} model
        prediction (apply knn/predict X y z options)]
    (classes (first prediction))))

(defmethod predict :naive-bayes [model z & options]
  (let [{:keys [parameters]
         {:keys [classes]} :dataset} model
        prediction (apply naive-bayes/predict parameters z options)]
    (classes (first prediction))))
