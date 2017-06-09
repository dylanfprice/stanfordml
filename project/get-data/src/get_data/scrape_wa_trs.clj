(ns scrape-wa-trs
  (:require [scrape-trs.core :refer [get-trip-reports]]))

(comment
  "TODO:
   - upgrade scrape-trs to have error handling strategy
     page could not load
     expected content could not be there
   - retrieve cc.com tr list pages for each region in WA (POST)
   - retrieve sp.com tr list page for WA
   - get-trip-reports on each page and concat all together
   - save to csv"

  (def cascade-climbers-washington-regions
    #{"Alpine Lakes"
      "Central/Eastern  Washington"
      "Columbia River Gorge"
      "Ice Climbing Forum"
      "Mount Rainier NP"
      "North Cascades"
      "Olympic Peninsula"
      "Rock Climbing Forum"
      "Southern WA Cascades"
      "the *freshiezone*"}))

(defn -main [& args])
