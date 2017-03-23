(ns analyze-data.tf-idf.n-grams-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.tf-idf.n-grams :as test-ns]))

(deftest to-words-test
  (is (= ["hello" "world"]
         (test-ns/to-words "hello world"))
      "separates two words")
  (is (= ["hello" "world"]
         (test-ns/to-words "Hello world"))
      "lowercases words")
  (is (= ["hello" "world"]
         (test-ns/to-words "Hello world!"))
      "removes punctuation...")
  (is (= ["he'll" "world"]
         (test-ns/to-words "he'll world"))
      "...but keeps apostrophes"))

(deftest remove-stopwords-test
  (is (= ["hello" "world"]
         (test-ns/remove-stopwords ["the" "hello" "world"]))
      "removes common word \"the\""))

(deftest n-grams-test
  (is (= ["a b" "b c"]
         (test-ns/n-grams 2 ["a" "b" "c"]))
      "creates bigrams"))
