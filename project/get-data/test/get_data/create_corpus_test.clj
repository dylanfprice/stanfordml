(ns get-data.create-corpus-test
  (:require [clojure.test :refer [deftest is]]
            [get-data.create-corpus :as test-ns]))

(deftest create-corpus-test
  (let [trip-reports [{"label" "a"
                       "title" "a trip report"
                       "text" "Sentence one. Sentence two. Sentence three."}]
        result (test-ns/create-corpus trip-reports)]
    (is (= 1 (count result))
        "returns sequence of same length as trip-reports")
    (is (= (set ["document-label" "document-text"])
           (-> result first keys set))
        "returned maps have correct keys")
    (is (= "a" ((first result) "document-label"))
        "document-label in returned map is the same as label")
    (is (= "a trip report\nSentence one. Sentence two."
           ((first result) "document-text"))
        "document-text is title + first two sentences of text"))
  (let [trip-reports [{"label" "a"
                       "title" "a trip report"
                       "text" "Sentence one."}]
        result (test-ns/create-corpus trip-reports)]
    (is (= "a trip report\nSentence one."
           ((first result) "document-text"))
        "includes just first sentence if there is only one"))
  (let [trip-reports [{"label" " "
                       "title" "a trip report"
                       "text" "Sentence one."}]
        result (test-ns/create-corpus trip-reports)]
    (is (= [] result)
        "filters out trip reports which aren't labelled")))
