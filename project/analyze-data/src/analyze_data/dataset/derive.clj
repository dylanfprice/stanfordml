(ns analyze-data.dataset.derive
  (:require [clojure.core.matrix :as m]))

(defn- split-n
  "Divide integer n into buckets according to fractions, which must add up to
  1. If the fractions do not divide n into even portions, the leftovers will
  be placed in the last bucket.

  E.g. => (split-n 100 [1/2 1/2])
       [50 50]"
  [n fractions]
  (let [nums-without-last (drop-last (map #(int (* n %))
                                                 fractions))
        num-leftover (- n (apply + nums-without-last))]
    (concat nums-without-last [num-leftover])))

(defn- create-split-dataset
  [dataset indices num-to-drop num-to-take]
  (let [{:keys [X y]} dataset
        selection (->> indices
                       (drop num-to-drop)
                       (take num-to-take))]
    (assoc dataset
           :X (m/select X selection :all)
           :y (m/select y selection :all))))

(defn split-dataset
  "Randomly split a dataset into multiple datasets based on fractions.
  Fractions must add up to 1.

  E.g. (split-dataset dataset 4/5 1/5) splits a dataset into two datasets, the
  first with 4/5 of the samples and the second with 1/5.

  Return a sequence of datasets."
  [dataset & fractions]
  (let [{:keys [X y]} dataset
        num-samples (first (m/shape X))
        indices (shuffle (range num-samples))
        splits (split-n num-samples fractions)]
    (loop [splits splits
           num-taken 0
           new-datasets []]
      (if (some? splits)
        (let [num-to-take (first splits)
              new-dataset (create-split-dataset dataset
                                                indices
                                                num-taken
                                                num-to-take)]
          (recur (next splits)
                 (+ num-taken num-to-take)
                 (conj new-datasets new-dataset)))
        new-datasets))))

; TODO: write partition-fractions function to replace split-n
; TODO: write tests
(defn partition-dataset
  "Randomly partition a dataset into a lazy sequence of k separate datasets.
  Ensures that each partition has at least two samples, so may return less
  than k datasets."
  [dataset k]
  (let [{:keys [X y]} dataset
        num-samples (first (m/shape X))
        partition-size (max (int (/ num-samples k)) 2)
        indices (shuffle (range num-samples))
        partitions (partition partition-size indices)]
    (for [selection partitions]
      (assoc dataset
             :X (m/select X selection :all)
             :y (m/select y selection :all)))))
