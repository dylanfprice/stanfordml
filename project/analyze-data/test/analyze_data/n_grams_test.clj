(ns analyze-data.n-grams-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.n-grams :as test-ns]))


(deftest to-words-test
  (is (= ["hello" "world"]
         (test-ns/to-words "hello world"))
      "separates two words")
  (is (= ["hello" "world"]
         (test-ns/to-words "Hello world"))
      "lowercases words")
  (is (= ["hello" "world" "!"]
         (test-ns/to-words "Hello world!"))
      "separates punctuation"))
