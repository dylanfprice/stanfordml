(ns analyze-data.tf-idf.core
  (:require [analyze-data.tf-idf.term-frequency
             :refer [normalized-term-frequency]]
            [analyze-data.tf-idf.inverse-document-frequency
             :refer [inverse-document-frequency]]
            [analyze-data.tf-idf.words
             :refer [n-grams remove-stopwords to-words]]))

(defn to-terms
  "Given a string representing a document, return a sequence of words,
  bigrams, and trigrams found in the document."
  [document]
  (let [words (remove-stopwords (to-words document))]
    (concat words (n-grams 2 words) (n-grams 3 words))))

(defn- tf-idf-document
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

(defn tf-idf
  "Calculates (term frequency * inverse document frequency) values for a corpus
  of documents.

  term-corpus: a sequence of term sequences. A term is a word, or bigram, or
               trigram, etc. Each term sequence should represent a single
               document in the corpus.

  Return a sequence of the form:
  [[\"a\" ...] [0.3 ...] [0.2 ...] ...]

  The first sequence is a sorted sequence of all terms found in the
  corpus. The rest are sequences of tf-idf values of those terms, for each
  document in term-corpus."
  [term-corpus]
  (let [all-terms (sort (distinct (reduce into term-corpus)))
        tf-corpus (map normalized-term-frequency term-corpus)
        idf (inverse-document-frequency tf-corpus)
        tf-idf-values (map (partial tf-idf-document idf all-terms) tf-corpus)]
    (cons all-terms tf-idf-values)))
