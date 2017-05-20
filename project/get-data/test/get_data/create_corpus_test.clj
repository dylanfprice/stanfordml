(ns get-data.create-corpus-test
  (:require [clojure.test :refer [deftest is]]
            [get-data.create-corpus :as test-ns]))

(deftest create-corpus-test
  (let [trip-reports [{"label" "A"
                       "title" "a trip report"
                       "text" "Sentence one. Sentence two. Sentence three."}]
        labels #{"a"}
        result (test-ns/create-corpus trip-reports labels)]
    (is (= (set ["document-label" "document-text"])
           (-> result first keys set))
        "returned maps have correct keys")
    (is (= "a" ((first result) "document-label"))
        "document-label in returned map is lowercased label")
    (is (= "a trip report\nSentence one. Sentence two."
           ((first result) "document-text"))
        "document-text is title + first two sentences of text"))
  (let [trip-reports [{"label" "A"
                       "title" "a trip report"
                       "text" "Sentence one."}]
        labels #{"a"}
        result (test-ns/create-corpus trip-reports labels)]
    (is (= "a trip report\nSentence one."
           ((first result) "document-text"))
        "includes just first sentence if there is only one"))
  (let [trip-reports [{"label" "B"
                       "title" "b trip report"
                       "text" "Sentence one."}]
        labels #{"a"}
        result (test-ns/create-corpus trip-reports labels)]
    (is (= [] result)
        "filters out trip reports which don't have specified label")))
