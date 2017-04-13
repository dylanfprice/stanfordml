(ns analyze-data.tf-idf.core-test
  (:require [clojure.core.matrix :as m]
            [clojure.test :refer [deftest is use-fixtures]]
            [analyze-data.tf-idf.core :as test-ns]))
(defn use-vectorz-fixture
  [f]
  (let [default-implementation (m/current-implementation)]
    (m/set-current-implementation :vectorz)
    (f)
    (m/set-current-implementation default-implementation)))

(use-fixtures :once use-vectorz-fixture)

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
  (is (= [3.0]
         (vec (test-ns/tf-idf-document
           ["test"]
           {"test" 3}
           ["test"])))
      "3 when idf is 3 and tf is 1")
  (is (= [0.0]
         (vec (test-ns/tf-idf-document
           ["test"]
           {}
           ["test"])))
      "0 when idf is 0")
  (is (= [0.0]
         (vec (test-ns/tf-idf-document
           ["test"]
           {"test" 3}
           [])))
      "0 when tf is 0")
  (is (= [3.0 2.0]
         (vec (test-ns/tf-idf-document
           ["test" "hello"]
           {"hello" 2 "test" 3}
           ["hello" "test"])))
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
