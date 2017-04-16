(ns analyze-data.tf-idf.core
  (:require [clojure.core.matrix :as m]
            [analyze-data.tf-idf.term-frequency
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
  inverse-document-frequency: map from term to its inverse document frequency
                              in the corpus
  term-document: sequence of terms representing a single document

  Return a core.matrix sparse array of calculated tf-idf values for the given
  term-document, matching the order of all-terms."
  [all-terms inverse-document-frequency term-document]
  (let [term-frequencies (normalized-term-frequency term-document)
        calc-tf-idf #(* (get term-frequencies % 0.0)
                        (get inverse-document-frequency % 0.0))
        array (m/new-sparse-array [(count all-terms)])
        indexed-tf-idf-values (eduction (map calc-tf-idf)
                                        (map-indexed #(vector %1 %2))
                                        (filter #(not= 0.0 (second %)))
                                        all-terms)]
    (doseq [[index value] indexed-tf-idf-values]
      (m/mset! array index value))
    array))

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

  :all-terms is a sorted sequence of all terms found in the corpus.
  :idf is a map from term to its inverse document frequency.
  :tf-idf is a lazy sequence of core.matrix sparse arrays which contain the
          tf-idf values for each document in term-corpus."
  [term-corpus]
  (let [all-terms (->> term-corpus (apply concat) (distinct) (sort))
        idf (inverse-document-frequency term-corpus)
        tf-idf-values (map (partial tf-idf-document all-terms idf)
                           term-corpus)]
    {:all-terms all-terms
     :idf idf
     :tf-idf tf-idf-values}))
