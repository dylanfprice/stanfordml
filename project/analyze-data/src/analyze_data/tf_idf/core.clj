(ns analyze-data.tf-idf.core
  (:require [analyze-data.tf-idf.term-frequency
             :refer [normalized-term-frequency]]
            [analyze-data.tf-idf.inverse-document-frequency
             :refer [inverse-document-frequency]]
            [analyze-data.tf-idf.words
             :refer [n-grams remove-stopwords to-words]]))

(defn to-terms
  "Turn document into a sequence of words, remove stopwords, and return a lazy
  sequence of terms based on term-types.

  valid term-types are
    :words
    :bigrams
    :trigrams
    (default is [:words])"
  [document & term-types]
  (let [words (-> document to-words remove-stopwords)
        term-types (or (not-empty (set term-types)) #{:words})]
    (concat (if (:words term-types) words [])
            (if (:bigrams term-types) (n-grams 2 words) [])
            (if (:trigrams term-types) (n-grams 3 words) []))))

(defn tf-idf-document
  "Calculate a sequence of (term frequency * inverse document frequency)
  values for a single document.

  all-terms: sorted sequence of all terms found in the corpus
  idf: map from term to its inverse document frequency in the corpus
  tf-document: map from term to its frequency in a single document

  Return a sequence of calculated tf-idf values for the given tf-document,
  matching the order of all-terms."
  [all-terms idf tf-document]
  (let [calc-tf-idf (fn [term] (* (get tf-document term 0) (get idf term 0)))]
    (map calc-tf-idf all-terms)))

(defn tf-idf
  "Calculates (term frequency * inverse document frequency) values for a
  corpus of documents.


  term-corpus: a sequence of term sequences. A term is a word, or bigram, or
               trigram, etc. Each term sequence should represent a single
               document in the corpus.

  Return a map of the form:
  {:all-terms [term1 term2 ...]
   :idf       {term1 value
               term2 value
               ...}
   :tf-idf    [[term1-value term2-value ...]
               [term1-value term2-value ...]
               ...]}

  :all-terms is a sorted sequence of all terms found in the corpus. :idf is a
  map from term to its inverse document frequency. :tf-idf contains sequences
  of tf-idf values matching the order of :terms, for each document in
  term-corpus."
  [term-corpus]
  (let [all-terms (sort (distinct (apply concat term-corpus)))
        tf-corpus (map normalized-term-frequency term-corpus)
        idf (inverse-document-frequency term-corpus)
        tf-idf-values (map (partial tf-idf-document all-terms idf) tf-corpus)]
    {:all-terms all-terms
     :idf idf
     :tf-idf tf-idf-values}))
