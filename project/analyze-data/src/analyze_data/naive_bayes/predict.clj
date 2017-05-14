(ns analyze-data.naive-bayes.predict
  (:require [clojure.core.matrix :as m]
            [analyze-data.greatest :refer [greatest]]))

(defn predict
  "parameters: map produced by analyze-data.naive-bayes.train/train
   z: vector of term weights representing a document

  Options:
    :threshold (default 0.00) specify a probability that must be exceeded to
               make a prediction (see below)

  Return a vector of [most-likely-label probability] or [nil nil] if the
  probability of the most likely label does not exceed :threshold."
  [parameters z & options]
  ; n = # features
  ; k = # classes
  ; log-phi is k x n, log-phi-y is k x 1
  ;
  ; p(y=a|z) = \frac{\prod_{z_i \ne 0}[ p(z_i|y=a) ] p(y=a)}
  ;                 {\sum_{j=1}^k \prod_{z_i \ne 0}[ p(z_i|y=j) ] p(y=j)}
  ;
  ;          = \frac{\exp[ \sum_{i=1}^n \log p(z_i|y=a) + \log p(y=a) - C ]}
  ;                 {\sum_{j=1}^k \exp[
  ;                    \sum_{i=1}^n \log p(z_i|y=j) + \log p(y=j) - C ]}
  (let [{:keys [log-phi log-phi-y]} parameters
        {:keys [threshold] :or {threshold 0.00}} options
        log-numerators (->> (m/mul log-phi z)
                        (map m/esum)
                        (m/add log-phi-y))
        scaled-numerators (-> log-numerators
                              (m/sub (m/emax log-numerators))
                              (m/exp))
        scaled-denominator (m/esum scaled-numerators)
        probs (m/div scaled-numerators scaled-denominator)
        indexed-probs (map-indexed #(vector %1 %2) probs)
        [most-likely-label probability] (reduce greatest indexed-probs)]
    (if (> probability threshold)
      [most-likely-label probability]
      [nil nil])))
