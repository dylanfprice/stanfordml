(ns scrape-trs.cascade-climbers.extract-tr-links
  (:require [clojure.string :as string]
            [reaver]
            [scrape-trs.ensure-sequence :refer [ensure-sequence]]))

(defn extract-tr-links
  "Given a string containing the trip reports page from cascade climbers,
  return a sequence of links representing the trip reports on that page."
  [page]
  (->> (reaver/extract (reaver/parse page)
                       []
                       ".t_inner td a"
                       (reaver/attr :href))
      (ensure-sequence)
      (filter (partial re-find
                       #"/forum/ubbthreads\.php\?ubb=showflat&Number=\d+"))))

