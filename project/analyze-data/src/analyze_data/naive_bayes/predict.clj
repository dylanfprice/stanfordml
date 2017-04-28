(ns analyze-data.naive-bayes.predict
  (:require [clojure.core.matrix :as m]
            [analyze-data.greatest :refer [greatest]]))

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
    (reduce greatest indexed-log-probs)))
