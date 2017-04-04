(ns analyze-data.tf-idf.term-frequency)

(defn term-frequency
  "Return a map from term to number of times it appears in terms."
  [terms]
  (reduce (fn [m term] (assoc m term (inc (get m term 0)))) {} terms))

(defn calc-normalized-term-frequency
  "Normalize term-frequency based on max-term-frequency."
  [term-frequency max-term-frequency]
  (+ 0.5 (* 0.5 (/ term-frequency max-term-frequency))))

(defn normalized-term-frequency
  "Like term-frequency, but prevents bias towards longer documents. See
  https://en.wikipedia.org/wiki/Tf–idf#Term_frequency_2"
  [terms]
  (let [term-frequencies (term-frequency terms)
        max-term-frequency (apply max 0 (vals term-frequencies))
        assoc-ntf (fn [m term] (assoc m
                                      term
                                      (calc-normalized-term-frequency
                                        (get term-frequencies term)
                                        max-term-frequency)))]
    (reduce assoc-ntf {} terms)))
