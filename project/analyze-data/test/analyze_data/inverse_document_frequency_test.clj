(ns analyze-data.inverse-document-frequency-test
  (:require [clojure.test :refer [deftest is]]
            [analyze-data.inverse-document-frequency :as test-ns]))

(deftest count-documents-test
  (is (= 0
         (test-ns/count-documents [{"a" 1}] "b"))
      "0 when no documents contain term")
  (is (= 1
         (test-ns/count-documents [{"b" 1}] "b"))
      "1 when a single document contains term")
  (is (= 1
         (test-ns/count-documents [{"a" 1} {"b" 1}] "b"))
      "only counts documents that contain term"))

(deftest inverse-document-frequency-math-test
  (is (= (Math/log 5)
         (test-ns/inverse-document-frequency-math 5 1))))

; TODO: flesh out this test
(deftest inverse-document-frquency-test
  (is (= {"a" (Math/log 2), "b" 0.0}
         (test-ns/inverse-document-frequency [{"a" 5 "b" 1} {"b" 1}]))))
