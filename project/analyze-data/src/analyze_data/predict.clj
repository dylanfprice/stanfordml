(ns analyze-data.predict
  (:require [clojure.core.matrix :as m]
            [analyze-data.create-dataset.core :refer [document-to-vector]]
            [analyze-data.knn.core :as knn]
            [analyze-data.naive-bayes.predict :as naive-bayes]))

(defmulti predict
  "Predict a label for the given document.

  model: previously trained model
  document: string
  options: specific to the model-type
    :knn see analyze-data.knn.core/knn
    :naive-bayes see analyze-data.naive-bayes.predict/predict

  Return the predicted label."
  (fn [model document & options] (:type model)))

(defmethod predict :knn [model document & options]
  (let [{dataset :dataset
         {:keys [X y labels]} :dataset} model
        z (document-to-vector dataset document)
        prediction (apply knn/predict X y z options)]
    (labels (first prediction))))

(defmethod predict :naive-bayes [model document & options]
  (let [{:keys [parameters dataset]
         {:keys [labels]} :dataset} model
        z (document-to-vector dataset document)
        prediction (naive-bayes/predict parameters z)]
    (labels (first prediction))))
