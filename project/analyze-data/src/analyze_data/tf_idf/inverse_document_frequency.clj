(ns analyze-data.tf-idf.inverse-document-frequency)

(defn document-frequency
  "Return a map from term to number of documents it appears in."
  [tf-corpus]
  (let [count-term (fn [m term] (assoc m term (inc (get m term 0))))
        term-keys (mapcat keys tf-corpus)]
    (reduce count-term {} term-keys)))

(defn calc-inverse-document-frequency
  "Calculate inverse document frequency.

  num-documents: the total number of documents
  num-documents-with-term: the number of documents containing the term we are
                           calculating for"
  [num-documents num-documents-with-term]
  (Math/log (/ num-documents num-documents-with-term)))

(defn inverse-document-frequency
  "Given
  terms: a sequence of all terms
  tf-corpus: a sequence of term-frequency maps where each one represents a
             single document

  Return a map from term to its inverse document frequency (as defined at
  https://en.wikipedia.org/wiki/Tf–idf#Inverse_document_frequency_2)."
  [terms tf-corpus]
  (let [num-documents (count tf-corpus)
        document-frequencies (document-frequency tf-corpus)
        assoc-idf (fn [m term num-documents-with-term]
                    (assoc m
                           term
                           (calc-inverse-document-frequency
                             num-documents
                             num-documents-with-term)))]
    (reduce-kv assoc-idf {} document-frequencies)))
