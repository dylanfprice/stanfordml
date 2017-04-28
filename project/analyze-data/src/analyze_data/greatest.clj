(ns analyze-data.greatest)

(defn greatest
  "Given two vectors of the form [key value], return the one with the greater
  value."
  [a b]
  (let [[a-index a-value] a
        [b-index b-value] b]
    (if (> a-value b-value) a b)))
