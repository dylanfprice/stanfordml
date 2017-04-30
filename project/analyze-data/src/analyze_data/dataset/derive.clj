(ns analyze-data.dataset.derive
  (:require [clojure.core.matrix :as m]
            [clojure.set :refer [difference]]))

(defn- split-n
  "Divide integer n into buckets according to fractions, which must add up to
  1. If the fractions do not divide n into even portions, the leftovers will
  be placed in the last bucket.

  E.g. => (split-n [1/2 1/2] 100)
       [50 50]"
  [fractions n]
  (let [nums-without-last (drop-last (map #(int (* n %))
                                                 fractions))
        num-leftover (- n (apply + nums-without-last))]
    (concat nums-without-last [num-leftover])))

(defn- partition-by-counts
  "Return a lazy sequence of lists where the first has (first counts)
  elements, the second (second counts) elements and so on. If the sum of
  counts is greater than (count coll), then after coll is exhausted the rest
  of the lists will be empty."
  [counts coll]
  (lazy-seq
    (when-let [n (first counts)]
      (cons (take n coll)
            (partition-by-counts (next counts) (drop n coll))))))

(defn- subset-dataset
  "Return a dataset with only the samples indexed by selection."
  [dataset selection]
  (let [{:keys [X y]} dataset]
    (assoc dataset
           :X (m/select X selection :all)
           :y (m/select y selection :all))))

(defn split-dataset
  "Randomly split a dataset into multiple datasets based on fractions.
  Fractions must add up to 1.

  E.g. (split-dataset [4/5 1/5] dataset) splits a dataset into two datasets,
  the first with approximately 4/5 of the samples.

  Return a sequence of datasets."
  [fractions dataset]
  (let [num-samples (-> dataset :X m/shape first)
        indices (shuffle (range num-samples))
        counts (split-n fractions num-samples)
        partitions (partition-by-counts counts indices)]
    (map (partial subset-dataset dataset) partitions)))

(defn- create-partition
  [dataset train-selection test-selection]
  [(subset-dataset dataset train-selection)
   (subset-dataset dataset test-selection)])

(defn partition-dataset-k-fold
  "Randomly partition a dataset into a sequence of k [train-dataset
  test-dataset] vectors. Each test-dataset will have a different 1/k portion
  of the original dataset. Tries to ensures that each dataset has at least two
  samples, so may return less than k datasets."
  [dataset k]
  (let [num-samples (-> dataset :X m/shape first)
        partition-size (max (int (/ num-samples k)) 2)
        indices (set (range num-samples))
        test-selections (->> indices (shuffle) (partition partition-size))]
    (map #(create-partition dataset (difference indices %) %)
         test-selections)))
