(ns tf-idf.core)

(defn to-tf-idf-document
  "Calculate a sequence of (term frequency * inverse document frequency) values
  for a single document.

  idf: map from term to its inverse document frequency in the corpus
  all-terms: sorted sequence of all terms found in the corpus
  tf-document: map from term to its frequency in a single document

  Return a sequence of calculated tf-idf values for the given tf-document,
  matching the order of all-terms."
  [idf all-terms tf-document]
  (let [calc-tf-idf (fn [term] (* (get tf-document term 0) (get idf term 0)))]
    (map calc-tf-idf all-terms)))

(defn to-tf-idf-corpus
  "Given
  term-corpus: a sequence of term sequences, where each term sequence
               represents a single document

  Return a sequence of the form:
  [[\"a\" ...] [0.3 ...] [0.2 ...] ...]

  where the first sequence is a sorted sequence of all terms found in the
  corpus, and the rest are tf-idf sequences for each document in term-corpus."
  [term-corpus]
  (let [all-terms (sort (distinct (apply concat term-corpus)))
        tf-corpus (map double-normalized-term-frequency term-corpus)
        idf (inverse-document-frequency tf-corpus)]
    (map (partial tf-idf idf all-terms) tf-corpus)))


; (with-open [in-file (io/reader "../scrape-summitpost-data/data.csv")]
;   (def row (apply zipmap (take 2 (csv/read-csv in-file)))))

; could be in a different namespace
(defn to-terms
  "Given a string representing a document, return a sequence of words, bigrams,
  and trigrams found in the document."
  [document]
  (let [words (to-words document)]
    (concat words (n-grams 2 words) (n-grams 3 words))))

(defn write-tf-idf-corpus!
  [writer documents]
  (let [term-corpus (map to-terms documents)
        tf-idf-corpus (to-tf-idf-corpus term-corpus)]
    (csv/write-csv writer tf-idf-corpus)))

