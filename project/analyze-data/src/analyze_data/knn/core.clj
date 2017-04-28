(ns analyze-data.knn.core
  (:require [clojure.core.matrix :as m]
            [analyze-data.greatest :refer [greatest]]))

(defn cosine-distance
  "Calculate 1 - cos(theta) between each vector in X and z, where theta is the
  angle between the vectors.

  X is a matrix of the form
  [[--x1--]
   [--x2--]
   ...]

  z is a core.matrix vector

  Return a sequence of [distance-from-x1 distance-from-x2 ...]."
  [X z]
  (let [dot-product (m/mmul X z)
        product-of-norms (m/mul (map m/magnitude X) (m/magnitude z))
        cosine-similarity (m/div dot-product product-of-norms)]
    (->> cosine-similarity
         (m/sub 1)
         (m/emap #(if (Double/isNaN %) 1.0 %)))))

(defn euclidean-distance
  "Euclidean distance between each vector in X and z.

  X is a matrix of the form
  [[--x1--]
   [--x2--]
   ...]

  z is a core.matrix vector

  Return a sequence of [distance-from-x1 distance-from-x2 ...]."
  [X z]
  (map (partial m/distance z) X))

(defn knn
  "Find k nearest neighbors to z in X.

  X is a matrix of the form
  [[--x1--]
   [--x2--]
   ...]

  z is a core.matrix vector

  Options
    :k (default 3)
    :distance-fn (default euclidean-distance)

  Return a sequence of the form
  [[nearest-x-index distance]
   [next-nearest-x-index distance]
   ...]"
  [X z & options]
  (let [{:keys [k distance-fn]
         :or {k 3, distance-fn euclidean-distance}} options]
    (->> (distance-fn X z)
         (map-indexed #(vector %1 %2))
         (sort-by second)
         (take k))))

(defn predict
  "Predict a label for for z based on a majority vote of its k nearest
  neighbors.

  X is a matrix of the form
  [[--x1--]
   [--x2--]
   ...]

  y is a vector of integer labels

  z is a core.matrix vector

  options are passed through to knn

  Return a vector of [most-likely-label num-votes]."
  [X y z & options]
  (let [nearest-neighbors (apply knn X z options)
        nearest-labels (->> nearest-neighbors (map first) (map #(y %)))
        neighbor-frequencies (frequencies nearest-labels)]
    (reduce greatest neighbor-frequencies)))
