(ns create-corpus.core-test
  (:require [clojure.test :refer [deftest is]]
            [create-corpus.core :as test-ns]))

(deftest create-corpus-test
  (let [trip-reports [{"label" "a"
                       "title" "a trip report"
                       "text" "Sentence one. Sentence two. Sentence three."}]
        labels #{"a"}
        result (test-ns/create-corpus trip-reports labels)]
    (is (= (set ["document-label" "document-text"])
           (-> result first keys set))
        "returned maps have correct keys")
    (is (= "a" ((first result) "document-label"))
        "document-label in returned map is label")
    (is (= "a trip report\nSentence one. Sentence two. Sentence three."
           ((first result) "document-text"))
        "document-text is title + text"))
  (let [trip-reports [{"label" "  a  "
                       "title" "a trip report"
                       "text" "Sentence one."}]
        labels #{"a"}
        result (test-ns/create-corpus trip-reports labels)]
    (is (= "a" ((first result) "document-label"))
        "lowercases and trims label of whitespace"))
  (let [trip-reports [{"label" "a"
                       "title" "a trip report"
                       "text" "Sentence one."}]
        labels #{"a"}
        result (test-ns/create-corpus trip-reports labels)]
    (is (= "a trip report\nSentence one."
           ((first result) "document-text"))
        "includes just first sentence if there is only one"))
  (let [trip-reports [{"label" "b"
                       "title" "b trip report"
                       "text" "Sentence one."}]
        labels #{"a"}
        result (test-ns/create-corpus trip-reports labels)]
    (is (= [] result)
        "filters out trip reports which don't have specified label")))
