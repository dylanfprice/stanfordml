(ns analyze-data.naive-bayes.predict
  (:require [clojure.core.matrix :as m]))

(defn get-greater
  "Given two vectors of the form [index value], return the one with the
  greater value."
  [a b]
  (let [[a-index a-value] a
        [b-index b-value] b]
    (if (> a-value b-value) a b)))

(defn predict
  "model: map produced by analyze-data.naive-bayes.train/train
   x: vector of term weights representing a document

  Return a vector of [most-likely-label score]"
  [model x]
  (let [{:keys [log-phi log-phi-y]} model
        log-probs (->> (m/mul log-phi x)
                       (map m/esum)
                       (m/add log-phi-y))
        indexed-log-probs (map-indexed #(vector %1 %2) log-probs)]
    (reduce get-greater indexed-log-probs)))
