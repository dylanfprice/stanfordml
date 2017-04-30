(ns analyze-data.create-dataset.tf-idf
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
  [dataset-type corpus]
  (let [{:keys [y classes]} (get-y-and-classes corpus)
        document-texts (map #(% "document-text") corpus)
        {:keys [all-terms tf-idf idf]} (tf-idf (map to-terms document-texts))]
    {:type dataset-type
     :X (create-sparse-matrix (count y) tf-idf)
     :y y
     :features all-terms
     :classes classes
     :extra {:inverse-document-frequencies idf}}))

(defn document-to-vector
  [dataset document]
  (let [{features :features
         {idf :inverse-document-frequencies} :extra} dataset]
    (->> document
         (to-terms)
         (tf-idf-document features idf)
         (m/sparse-array))))
