(ns analyze-data.test-fixtures
  (:require [clojure.core.matrix :as m]))

(defn use-vectorz
  [f]
  (let [default-implementation (m/current-implementation)]
    (m/set-current-implementation :vectorz)
    (f)
    (m/set-current-implementation default-implementation)))
