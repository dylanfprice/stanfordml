(ns analyze-data.tf-idf.term-frequency)

(defn term-frequency
  "Return a map from term to number of times it appears."
  [terms]
  (reduce (fn [m term] (assoc m term (inc (get m term 0)))) {} terms))

(defn double-normalized-term-frequency
  "Like term-frequency, but prevents bias towards longer documents. See
  https://en.wikipedia.org/wiki/Tf–idf#Term_frequency_2"
  [terms]
  (let [tf (term-frequency terms)
        max-tf (apply max 0 (vals tf))
        normalized-tf (fn [term]
                        (+ 0.5 (* 0.5 (/ (get tf term) max-tf))))
        normalized-tf-entry (fn [term] [term (normalized-tf term)])]
    (apply hash-map (mapcat normalized-tf-entry terms))))
