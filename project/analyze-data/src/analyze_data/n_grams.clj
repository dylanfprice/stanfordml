(ns analyze-data.n-grams
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as string]))

; (with-open [in-file (io/reader "../scrape-summitpost-data/data.csv")]
;   (def row (apply zipmap (take 2 (csv/read-csv in-file)))))

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

(defn term-frequency
  "Return a map from word to number of times it appears."
  [words]
  (reduce (fn [m word] (assoc m word (inc (get m word 0)))) {} words))

(defn double-normalization-term-frequency
  "Like term-frequency, but prevents bias towards longer documents. See
  https://en.wikipedia.org/wiki/Tf%E2%80%93idf#Term_frequency"
  [words]
  (let [tf (term-frequency words)
        max-tf (max (vals tf))
        normalized-tf (fn [term] (+ 0.5 (* 0.5
                                           (/ (get tf term) max-tf))))]
    (apply hash-map (mapcat normalized-tf words))))



