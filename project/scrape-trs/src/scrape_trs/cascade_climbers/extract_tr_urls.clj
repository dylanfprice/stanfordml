(ns scrape-trs.cascade-climbers.extract-tr-urls
  (:require [clojure.string :as string]
            [reaver]))

(def tr-table-index
  "how many t_outer tables into the page the table with trip reports is"
  9)

(def washington-regions
  #{"Alpine Lakes"
    "Central/Eastern  Washington"
    "Columbia River Gorge"
    "Ice Climbing Forum"
    "Mount Rainier NP"
    "North Cascades"
    "Olympic Peninsula"
    "Rock Climbing Forum"
    "Southern WA Cascades"
    "the *freshiezone*"})

(defn extract-tr-urls
  "Given a string containing the trip reports page from cascade climbers,
  return a sequence of urls representing the trip reports on that page."
  [page]
  (let [tr-table (-> (reaver/select (reaver/parse page) ".t_outer")
                     (nth tr-table-index))
        table-rows (->> (reaver/select tr-table "td[class^=alt]")
                        (partition 5))
        wa-rows (filter (fn [[date tr-type location region posted-by]]
                          (washington-regions (reaver/text region)))
                        table-rows)
        tr-urls (for [[date tr-type location region posted-by] wa-rows]
                  (reaver/extract location [] "a" (reaver/attr :href)))]
    tr-urls))
