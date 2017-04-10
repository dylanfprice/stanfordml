(ns analyze-data.knn.core
  (:require [clojure.core.matrix :as m]))

(defn cosine-similarity
  "TODO: this is the cosine of the angle between the two vectors?

  X is a matrix of the form
  [[--x1--]
   [--x2--]
   ...]

  y is a vector

  Return a vector of [distance-from-x1 distance-from-x2 ...]."
  [X y]
  (m/div
    (m/sub 1 (m/mmul X y))
    (m/mul (map m/magnitude X)
           (m/magnitude y))))

(defn euclidean-distance
  "Euclidean distance between each row of a matrix and a vector.

  X is a matrix of the form
  [[--x1--]
   [--x2--]
   ...]

  y is a vector

  Return a vector of [distance-from-x1 distance-from-x2 ...]."
  [X y]
  (map (partial m/distance y) X))

(defn knn
  "Find k nearest neighbors to y in X.

  X is a matrix of the form
  [[--x1--]
   [--x2--]
   ...]

  y is a vector

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
