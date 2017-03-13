(ns analyze-data.term-frequency)

(defn term-frequency
  "Return a map from word to number of times it appears."
  [words]
  (reduce (fn [m word] (assoc m word (inc (get m word 0)))) {} words))

(defn double-normalized-term-frequency
  "Like term-frequency, but prevents bias towards longer documents. See
  https://en.wikipedia.org/wiki/Tf–idf#Term_frequency_2"
  [words]
  (let [tf (term-frequency words)
        max-tf (apply max (vals tf))
        normalized-tf (fn [term]
                        (+ 0.5 (* 0.5 (/ (get tf term) max-tf))))
        normalized-tf-entry (fn [term] [term (normalized-tf term)])]
    (apply hash-map (mapcat normalized-tf-entry words))))
