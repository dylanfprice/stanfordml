(ns analyze-data.core
  (:require [clojure.core.matrix :as m]
            [clojure.string :as string]
            [analyze-data.dataset.create.core :refer [create-dataset-file!]]
            [analyze-data.evaluate-model
             :refer [k-fold-cross-validation
                     progressively-train-and-evaluate]]
            [analyze-data.knn.core :refer [cosine-distance]]
            [analyze-data.score-model :refer [accuracy f1-score]]
            [analyze-data.serialize :refer [read-object]]))

(defn create-dataset!
  [f]
  (m/set-current-implementation :vectorz)
  (create-dataset-file!
   :tf-idf
   f
   (string/replace f #"\.csv$" ".dataset")
   :term-types [:words]
   :df-threshold 0))

(defn- get-options
  [model-type]
  (case model-type
    :knn {:k 3, :distance-fn cosine-distance}
    :naive-bayes {}))

(defn- analyze-confusion-matrix!
  [dataset confusion-matrix]
  (let [{:keys [y classes]} dataset]
    (with-out-str
      (println "Classes:")
      (doseq [c classes]
        (let [class-count (count (filter (partial = (.indexOf classes c)) y))]
          (println "  " (str c ":") class-count)))
      (println "Confusion matrix:")
      (doseq [c classes]
        (println "  " (str c ":") (confusion-matrix c)))
      (println "Accuracy:" (->> confusion-matrix accuracy float (format "%.2f")))
      (println "F1 Scores:")
      (let [f1-scores (map (partial f1-score confusion-matrix) classes)]
        (doseq [[c f1] (zipmap classes f1-scores)]
          (println "  " (str c ":") (->> f1 float (format "%.2f"))))))))

(defn evaluate-dataset!
  [dataset-file model-type]
  (m/set-current-implementation :vectorz)
  (let [dataset (read-object dataset-file)
        k 10
        confusion-matrix (apply k-fold-cross-validation
                                k
                                model-type
                                dataset
                                (get-options model-type))]
    (println (name model-type) (str k "-fold cross-validation on") dataset-file)
    (println (analyze-confusion-matrix! dataset confusion-matrix))))

(defn progressively-evaluate-dataset!
  [dataset-file model-type]
  (m/set-current-implementation :vectorz)
  (let [dataset (read-object dataset-file)
        k 30]
    (println "num samples," "test accuracy," "train accuracy")
    (doseq [{:keys [num-samples test-confusion-matrix train-confusion-matrix]}
            (apply progressively-train-and-evaluate
                   k model-type dataset (get-options model-type))]
      (print num-samples ", ")
      (print (->> test-confusion-matrix accuracy float (format "%.2f")) ", ")
      (println (->> train-confusion-matrix accuracy float (format "%.2f"))))))
