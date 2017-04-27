(ns analyze-data.naive-bayes.train
  (:require [clojure.core.matrix :as m]))

(defn- group-indices-by-value
  "y: a vector

  Return a sorted map from value in y to a vector of indices into y. The
  entry for key k will index the values in y which are equal to k."
  [y]
  (let [assoc-index (fn [m index value]
                      (let [indices (get m value [])]
                        (assoc m value (conj indices index))))]
    (reduce-kv assoc-index (sorted-map) y)))

(defn- sum-samples-by-label
  "X: design matrix
   label-indices: sorted map where key i looks up indices of samples labelled
                  i. It must be true that (= (keys label-indices) (range k))
                  for some k.

  Return a lazy sequence of core.matrix vectors where the ith vector is the
  sum of all samples labelled i."
  [X label-indices]
  ; preconditions
  (if-let [classes (keys label-indices)]
    (assert (= classes (range (+ 1 (m/emax classes))))))
  ; function
  (for [i (keys label-indices)]
    (let [samples (m/select X (get label-indices i) :all)]
      (apply m/add samples))))

(defn- calc-phi
  "X: design matrix
   label-indices: sorted map where key i looks up indices of samples labelled
                  i. It must be true that (= (keys label-indices) (range k))
                  for some k.

  Return a k x n matrix 'phi' where k is the number of class labels and n is
  the number of terms. Each entry phi[i][j] represents prob(j|y=i)."
  [X label-indices]
  (let [sum-of-samples-by-label (sum-samples-by-label X label-indices)
        sum-of-all-samples (apply m/add X)
        num-terms (second (m/shape X))]
    (m/div (m/add sum-of-samples-by-label 1)
           (m/add sum-of-all-samples num-terms))))

(defn- calc-phi-y
  "label-indices: sorted map where key i looks up indices of samples labelled
                  i. It must be true that (= (keys label-indices) (range k))
                  for some k.
   num-samples: number of samples

  Return a vector 'phi-y' of length k where k is the number of class labels.
  Each entry phi-y[i] represents p(y=i). "
  [label-indices num-samples]
  (let [num-docs-in-class (map #(count (second %)) label-indices)]
    (m/div num-docs-in-class num-samples)))

(defn train
  "Train a multinomial naive bayes classifier on given training data.

   X: m x n design matrix of the form
      [--x(1)--
       --x(2)--
       ...
       --x(m)--]
      where each x(i) is a vector of length n
      thus m is the number of samples and n is the number of terms
   y: m x 1 vector of class labels, which must be integers

  Return a map with keys :log-phi and :log-phi-y.
  :log-phi is a k x n matrix where k is the number of class labels and n is
    the number of terms. Each (i,j) entry represents log p(j|y=i), that is,
    the log of the conditional probability that term j appears in a document
    given its class label i.
  :log-phi-y is a length k vector where the ith entry represents log p(y=i),
    that is, the log of the posterior probability that a document is in class
    i."
  [X y]
  (let [label-indices (group-indices-by-value y)
        num-samples (first (m/shape X))]
    {:log-phi (m/log (calc-phi X label-indices))
     :log-phi-y (m/log (calc-phi-y label-indices num-samples))}))
