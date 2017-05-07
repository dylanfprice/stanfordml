(ns scrape-trs.cascade-climbers.extract-tr-urls
  (:require [clojure.string :as string]
            [reaver]
            [scrape-trs.ensure-sequence :refer [ensure-sequence]]))

(defn extract-tr-urls
  "Given a string containing the trip reports page from cascade climbers,
  return a sequence of urls representing the trip reports on that page."
  [page]
  (->> (reaver/extract (reaver/parse page)
                       []
                       ".t_inner td a"
                       (reaver/attr :href))
      (ensure-sequence)
      (filter (partial re-find
                       #"/forum/ubbthreads\.php\?ubb=showflat&Number=\d+"))))

