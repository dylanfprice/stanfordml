(ns analyze-data.dataset.derive-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [analyze-data.dataset.derive :as test-ns]
            [analyze-data.test-fixtures :refer [use-vectorz]]))

(use-fixtures :once use-vectorz)

(deftest split-n-test
  (is (= [50 50]
         (#'test-ns/split-n 100 [1/2 1/2]))
      "returns even split when fractions split num-samples evenly")
  (is (= [33 33 34]
         (#'test-ns/split-n 100 [1/3 1/3 1/3]))
      (str "last entry has leftovers when fractions don't split num-samples "
           "evenly")))

(def dataset
  {:type :tf-idf
   :X [[1 1] [1 0.5] [1 0]]
   :y [0 0 1]
   :features ["bar" "foo"]
   :classes ["one" "two"]
   :extra {:inverse-document-frequencies
           {"bar" 1, "foo" 1}}})

(deftest create-split-dataset-test
  (testing (str "returns a new dataset with subset of X and y according to "
                "num-to-drop and num-to-take")
    (let [new-dataset (#'test-ns/create-split-dataset dataset
                                                      [0 1 2]
                                                      0
                                                      2)]
      (is (= [0 0] (:y new-dataset)))
      (is (= [[1 1] [1 0.5]] (:X new-dataset)))))
  (testing (str "returns a new dataset taking subset of X and y in order "
                "specified by indices")
    (let [new-dataset (#'test-ns/create-split-dataset dataset
                                                      [2 1 0]
                                                      0
                                                      1)]
      (is (= [1] (:y new-dataset)))
      (is (= [[1 0]] (:X new-dataset))))))

(deftest split-dataset-test
  (let [new-datasets (test-ns/split-dataset dataset 2/3 1/3)]
    (is (= (count new-datasets) 2)
        "splits into n datasets because n fractions were specified")
    (is (= (dissoc dataset :X :y)
           (dissoc (first new-datasets) :X :y)
           (dissoc (second new-datasets) :X :y))
        "new datasets only differ in X and y data")
    (testing "returns 2/3 of samples in first dataset"
      (is (= (count (-> new-datasets first :X)) 2))
      (is (= (count (-> new-datasets first :y)) 2)))
    (testing "returns 1/3 of samples in second dataset"
      (is (= (count (-> new-datasets second :X)) 1))
      (is (= (count (-> new-datasets second :y)) 1)))
    (is (= (set (:X dataset))
           (set (concat (-> new-datasets first :X)
                        (-> new-datasets second :X))))
        "returned datasets have same data as original")))
