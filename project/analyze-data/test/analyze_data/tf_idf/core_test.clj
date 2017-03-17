(ns analyze-data.tf-idf.core-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.tf-idf.core :as test-ns]))

(deftest to-terms-test
  (is (= ["hello" "world" "test"
          ["hello" "world"] ["world" "test"]
          ["hello" "world" "test"]]
         (test-ns/to-terms "hello world test"))
      "returns words, bigrams, and trigrams"))

(deftest tf-idf-document
  (is (= [3]
         (#'test-ns/tf-idf-document
           {"test" 3}
           ["test"]
           {"test" 1}))
      "multiplies tf by idf")
  (is (= [0]
         (#'test-ns/tf-idf-document
           {}
           ["test"]
           {"test" 1}))
      "uses 0 for idf when missing")
  (is (= [0]
         (#'test-ns/tf-idf-document
           {"test" 3}
           ["test"]
           {}))
      "uses 0 for tf when missing")
  (is (= [2 3]
         (#'test-ns/tf-idf-document
           {"hello" 2 "test" 3}
           ["hello" "test"]
           {"hello" 1 "test" 1}))
      "matches the order of all-terms"))

(deftest tf-idf
  (is (= ["a" "b" "c"]
         (first (test-ns/tf-idf [["a" "b"] ["a" "c"] ["b" "a"]])))
      "first sequence is a sorted sequence of terms")
  (is (->> (test-ns/tf-idf [["a" "b"] ["a" "c"] ["b" "a"]])
           (rest)
           (concat)
           (map #(= float (type %)))
           (and))
      "rest of the sequences contain floats"))
