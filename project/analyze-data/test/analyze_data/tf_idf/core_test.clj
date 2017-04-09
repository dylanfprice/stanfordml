(ns analyze-data.tf-idf.core-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.tf-idf.core :as test-ns]))

(deftest to-terms-test
  (is (= []
         (test-ns/to-terms "the"))
      "removes stopwords")
  (is (= ["hello" "world" "test"]
         (test-ns/to-terms "hello world test"))
      "returns a sequence of words")
  (is (= ["hello world" "world test"]
         (test-ns/to-terms "hello world test" :bigrams))
      "returns bigrams when :bigrams specified")
  (is (= ["hello world test"]
         (test-ns/to-terms "hello world test" :trigrams))
      "returns trigrams when :trigrams specified")
  (is (= ["hello" "world" "test"
          "hello world" "world test"
          "hello world test"]
         (test-ns/to-terms "hello world test" :words :bigrams :trigrams))
      "returns words, bigrams, and trigrams when all specified"))

(deftest tf-idf-document
  (is (= [3]
         (test-ns/tf-idf-document
           ["test"]
           {"test" 3}
           {"test" 1}))
      "multiplies tf by idf")
  (is (= [0]
         (test-ns/tf-idf-document
           ["test"]
           {}
           {"test" 1}))
      "uses 0 for idf when missing")
  (is (= [0]
         (test-ns/tf-idf-document
           ["test"]
           {"test" 3}
           {}))
      "uses 0 for tf when missing")
  (is (= [3 2]
         (test-ns/tf-idf-document
           ["test" "hello"]
           {"hello" 2 "test" 3}
           {"hello" 1 "test" 1}))
      "matches the order of all-terms"))

(deftest tf-idf
  (let [term-corpus [["a" "b"] ["a" "c"] ["b" "a"]]
        result (test-ns/tf-idf term-corpus)]
    (is (= ["a" "b" "c"]
           (:all-terms result))
        ":all-terms is a sorted sequence of terms")
    (is (every? (partial contains? (:idf result))
                ["a" "b" "c"])
        ":idf contains a key for every term")
    (is (every? #(= java.lang.Double (type %))
                (->> result :tf-idf (apply concat)))
        ":tf-idf sequences contain doubles")))
