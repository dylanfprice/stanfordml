(ns scrape-trs.core-test
  (:require [clojure.test :refer [deftest is]]
            [scrape-trs.core :as test-ns]))

(deftest maps-to-vectors-test
  (is (= '([]) (#'test-ns/maps-to-vectors []))
      "returns sequence of one empty row when given empty sequence")
  (is (= '(["a", "b"], [1, 2])
         (#'test-ns/maps-to-vectors [{:a 1 :b 2}]))
      "returns sequence of rows when given map"))
