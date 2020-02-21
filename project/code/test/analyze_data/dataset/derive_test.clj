(ns analyze-data.dataset.derive-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [analyze-data.dataset.derive :as test-ns]
            [analyze-data.test-fixtures :refer [use-vectorz]]))

(use-fixtures :once use-vectorz)

(deftest split-n-test
  (is (= [50 50]
         (#'test-ns/split-n [1/2 1/2] 100))
      "returns even split when fractions split num-samples evenly")
  (is (= [33 33 34]
         (#'test-ns/split-n [1/3 1/3 1/3] 100))
      (str "last entry has leftovers when fractions don't split num-samples "
           "evenly")))

(deftest partition-by-counts-test
  (is (= [] (#'test-ns/partition-by-counts [] []))
      "returns empty sequence given empty counts and coll")
  (is (= [[1 2] [3 4]] (#'test-ns/partition-by-counts [2 2] [1 2 3 4]))
      (str "return sequence of lists where first has (first counts) elements "
           "etc."))
  (is (= [[1 2]] (#'test-ns/partition-by-counts [2] [1 2 3 4]))
      (str "returns less than the full list when sum of counts is less than "
           "length of coll."))
  (is (= [[1 2] [3 4] []] (#'test-ns/partition-by-counts [2 2 2] [1 2 3 4]))
      "returns empty lists after coll is exhausted"))

(def dataset
  {:type :tf-idf
   :X [[1 1] [1 0.5] [1 0] [0.5 1]]
   :y [0 0 1 1]
   :features ["bar" "foo"]
   :classes ["one" "two"]
   :extra {:inverse-document-frequencies
           {"bar" 1, "foo" 1}}})

(deftest subset-dataset-test
  (testing (str "returns a new dataset with subset of X and y according to "
                "selection")
    (let [new-dataset (#'test-ns/subset-dataset dataset [0 1])]
      (is (= [[1 1] [1 0.5]] (:X new-dataset)))
      (is (= [0 0] (:y new-dataset))))))

(deftest split-dataset-test
  (let [new-datasets (test-ns/split-dataset [3/4 1/4] dataset)]
    (is (= (count new-datasets) 2)
        "splits into n datasets because n fractions were specified")
    (is (= (dissoc dataset :X :y)
           (dissoc (first new-datasets) :X :y)
           (dissoc (second new-datasets) :X :y))
        "new datasets only differ in X and y data")
    (testing "returns 2/3 of samples in first dataset"
      (is (= (count (-> new-datasets first :X)) 3))
      (is (= (count (-> new-datasets first :y)) 3)))
    (testing "returns 1/3 of samples in second dataset"
      (is (= (count (-> new-datasets second :X)) 1))
      (is (= (count (-> new-datasets second :y)) 1)))
    (is (= (set (:X dataset))
           (set (concat (-> new-datasets first :X)
                        (-> new-datasets second :X))))
        "returned datasets have same data as original")))

(deftest partition-dataset-k-fold-test
  (let [partitions (test-ns/partition-dataset-k-fold dataset 2)]
    (is (= 2 (count partitions))
        "divides dataset into k partitions")
    (is (= (dissoc dataset :X :y)
           (dissoc (ffirst partitions) :X :y))
        "new datasets only differ in X and y data")
    (is (= (set (:X dataset))
           (set (mapcat :X (first partitions))))
        "each partition is equal to original dataset")
    (is (= 2 (-> partitions first second :y count))
        "each test-dataset has 1/k (1/2 * 4) of the data"))
  (let [partitions (test-ns/partition-dataset-k-fold dataset 4)]
    (is (= 2 (count partitions))
        "returns less than k partitions to ensure two samples per dataset")))
