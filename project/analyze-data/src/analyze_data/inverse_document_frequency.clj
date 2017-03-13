(ns analyze-data.inverse-document-frequency)

(defn count-documents
  "Return a count of how many documents in tf-corpus contain word."
  [tf-corpus word]
  (->> tf-corpus
       (filter #(contains? % word))
       (count)))

(defn inverse-document-frequency-math
  "Calculates inverse document frequency given the n, the total number of
  documents, and documents_with_word, the number of documents containing the
  word we are calculating for."
  [n documents_with_word]
  (Math/log (/ n documents_with_word)))

(defn inverse-document-frequency
  "Return a map from word to its inverse document frequency (as defined at
  https://en.wikipedia.org/wiki/Tf–idf#Inverse_document_frequency_2).
  tf-corpus is a sequence of term-frequency maps where each one represents a
  single document."
  [tf-corpus]
  (let [words (->> tf-corpus (map keys) (apply concat) (set))
        n (count tf-corpus)
        documents_with_word (map (partial count-documents tf-corpus) words)]
    (->> documents_with_word
         (map #(vector %1 (inverse-document-frequency-math n %2)) words)
         (apply concat)
         (apply hash-map))))
