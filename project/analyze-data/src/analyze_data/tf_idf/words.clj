(ns analyze-data.tf-idf.words
  (:require [clojure.string :as string]
            [clojure.java.io :refer [resource]]))

(def stopwords (delay (-> "stopwords.txt"
                          (resource)
                          (slurp)
                          (string/split #"\n")
                          (set))))

(defn to-words
  "Tokenize a string into a sequence of words."
  [text]
  (->> text
       (string/lower-case)
       (re-seq #"[a-z']+")))

(defn remove-stopwords
  "Remove very common words from a sequence of words. The set of stopwords
  can be found at words/stopwords."
  [words]
  (remove @stopwords words))

(defn n-grams
  "Partition a sequence of words into n-grams of size n."
  [n words]
  (->> words
       (partition n 1)
       (map (partial string/join " "))))
