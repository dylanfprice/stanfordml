(ns analyze-data.tf-idf.inverse-document-frequency)

(defn document-frequency
  "Return a map from term to number of documents it appears in."
  [term-corpus]
  (let [count-term (fn [m term] (assoc m term (inc (get m term 0))))
        unique-terms-per-document (mapcat set term-corpus)]
    (reduce count-term {} unique-terms-per-document)))

(defn calc-inverse-document-frequency
  "Calculate inverse document frequency.

  num-documents: the total number of documents
  num-documents-with-term: the number of documents containing the term we are
                           calculating for"
  [num-documents num-documents-with-term]
  (Math/log (/ num-documents num-documents-with-term)))

(defn singleton-terms
  "Given map from term to number of documents it appears in, return terms
  whose document frequency is 1."
  [document-frequencies]
  (->> document-frequencies
       (filter #(= 1 (val %)))
       (map key)))

(defn inverse-document-frequency
  "Given
  term-corpus: a sequence of term sequences. A term is a word, or bigram, or
               trigram, etc. Each term sequence should represent a single
               document in the corpus.
  options:
    :remove-singleton-terms? (default false) if true, remove all terms that
                             occur in only one document

  Return a map from term to its inverse document frequency (as defined at
  https://en.wikipedia.org/wiki/Tf–idf#Inverse_document_frequency_2)."
  [term-corpus & options]
  (let [{:keys [remove-singleton-terms?]
         :or [remove-singleton-terms? false]} options
        num-documents (count term-corpus)
        document-frequencies (document-frequency term-corpus)
        document-frequencies (if remove-singleton-terms?
                               (apply dissoc
                                      document-frequencies
                                      (singleton-terms document-frequencies))
                               document-frequencies)
        assoc-idf (fn [m term num-documents-with-term]
                    (assoc m
                           term
                           (calc-inverse-document-frequency
                             num-documents
                             num-documents-with-term)))]
    (reduce-kv assoc-idf {} document-frequencies)))
