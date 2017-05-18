(ns analyze-data.dataset.create.tf-idf
  (:require [clojure.core.matrix :as m]
            [analyze-data.create-sparse-matrix
             :refer [create-sparse-matrix]]
            [analyze-data.tf-idf.core
             :refer [tf-idf tf-idf-document to-terms]]))

(defn- get-y-and-classes
  [corpus]
  (let [document-labels (mapv #(% "document-label") corpus)
        classes (vec (sort (distinct document-labels)))
        lookup-class-index (reduce-kv (fn [m k v] (assoc m v k))
                                      {}
                                      classes)
        y (mapv (partial lookup-class-index) document-labels)]
    {:y y :classes classes}))

(defn create-dataset
  [dataset-type corpus & options]
  (let [{:keys [term-types remove-singleton-terms?]
         :or {term-types [:words], remove-singleton-terms? false}} options
        {:keys [y classes]} (get-y-and-classes corpus)
        document-texts (map #(% "document-text") corpus)
        document-terms (map #(apply to-terms % term-types) document-texts)
        {:keys [all-terms tf-idf idf]} (tf-idf document-terms
                                               :remove-singleton-terms?
                                               remove-singleton-terms?)]
    {:type dataset-type
     :X (create-sparse-matrix (count y) tf-idf)
     :y y
     :features all-terms
     :classes classes
     :extra {:inverse-document-frequencies idf
             :term-types term-types}}))

(defn document-to-vector
  [dataset document]
  (let [{features :features
         {:keys [inverse-document-frequencies term-types]} :extra} dataset]
    (->> (apply to-terms document term-types)
         (tf-idf-document features inverse-document-frequencies)
         (m/sparse-array))))
