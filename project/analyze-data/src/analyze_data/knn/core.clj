(ns analyze-data.knn.core
  (:require [clojure.core.matrix :as m]))

(defn cosine-distance
  "Calculate 1 - cos(theta) between each vector in X and y, where theta is the
  angle between the vectors.

  X is a matrix of the form
  [[--x1--]
   [--x2--]
   ...]

  y is a core.matrix vector

  Return a sequence of [distance-from-x1 distance-from-x2 ...]."
  [X y]
  (let [dot-product (m/mmul X y)
        product-of-norms (m/mul (map m/magnitude X) (m/magnitude y))
        cosine-similarity (m/div dot-product product-of-norms)]
    (->> cosine-similarity
         (m/sub 1)
         (m/emap #(if (Double/isNaN %) 1.0 %)))))

(defn euclidean-distance
  "Euclidean distance between each vector in X and y.

  X is a matrix of the form
  [[--x1--]
   [--x2--]
   ...]

  y is a core.matrix vector

  Return a sequence of [distance-from-x1 distance-from-x2 ...]."
  [X y]
  (map (partial m/distance y) X))

(defn knn
  "Find k nearest neighbors to y in X.

  X is a matrix of the form
  [[--x1--]
   [--x2--]
   ...]

  y is a core.matrix vector

  Options
    :k (default 3)
    :distance-fn (default euclidean-distance)

  Return a sequence of the form
  [[nearest-x-index distance]
   [next-nearest-x-index distance]
   ...]"
  [X y & options]
  (let [{:keys [k distance-fn]
         :or {k 3, distance-fn euclidean-distance}} options]
    (->> (distance-fn X y)
         (map-indexed #(vector %1 %2))
         (sort (comparator #(< (second %1) (second %2))))
         (take k))))
