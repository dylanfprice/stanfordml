(ns tf-idf.inverse-document-frequency)

(defn count-documents
  "Return a count of how many documents in tf-corpus contain term."
  [tf-corpus term]
  (->> tf-corpus
       (filter #(contains? % term))
       (count)))

(defn inverse-document-frequency-math
  "Calculates inverse document frequency given the n, the total number of
  documents, and documents_with_term, the number of documents containing the
  term we are calculating for."
  [n documents_with_term]
  (Math/log (/ n documents_with_term)))

(defn inverse-document-frequency
  "Return a map from term to its inverse document frequency (as defined at
  https://en.wikipedia.org/wiki/Tf–idf#Inverse_document_frequency_2).
  tf-corpus is a sequence of term-frequency maps where each one represents a
  single document."
  [tf-corpus]
  (let [terms (->> tf-corpus (map keys) (apply concat) (set))
        n (count tf-corpus)
        documents_with_term (map (partial count-documents tf-corpus) terms)]
    (->> documents_with_term
         (map #(vector %1 (inverse-document-frequency-math n %2)) terms)
         (apply concat)
         (apply hash-map))))
