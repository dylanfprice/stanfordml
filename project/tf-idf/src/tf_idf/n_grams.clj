(ns tf-idf.n-grams
  (:require [clojure.string :as string]))

(defn to-words
  "Tokenize a string into a sequence of words and punctuation."
  [text]
  (->> text
       (string/lower-case)
       (re-seq #"\p{Lower}+|\p{Punct}")))

(defn n-grams
  "Partition a sequence of words into n-grams of size n."
  [n words]
  (partition n 1 words))
