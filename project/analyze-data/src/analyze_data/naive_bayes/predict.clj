(ns analyze-data.naive-bayes.predict
  (:require [clojure.core.matrix :as m]
            [analyze-data.greatest :refer [greatest]]))

(defn predict
  "parameters: map produced by analyze-data.naive-bayes.train/train
   z: vector of term weights representing a document

  Return a vector of [most-likely-label score]"
  [parameters z]
  (let [{:keys [phi phi-y]} parameters
        probs (->> (m/mul phi z)
                       (map m/esum)
                       (m/add phi-y))
        indexed-probs (map-indexed #(vector %1 %2) probs)]
    (reduce greatest indexed-probs)))
