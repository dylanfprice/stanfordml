(ns scrape-data.core
  (:gen-class)
  (:require [scrape-data.summitpost :as summitpost]
            [reaver]
            [clojure.java.io :as io]))



(def summitpost-wa-mountains "/object_list.php?object_type=1&state_province_1=Washington&sort_select_1=object_name")


(defn- save-summitpost-item [item-url]
  (let [file-name (summitpost/extract-item-name item-url)
        file-contents (slurp item-url)]
    (spit (str "data/" file-name) file-contents)))

(defn -main [& args]
  (.mkdir (io/file "data"))
  (map save-summitpost-item
       (summitpost/get-search-result-urls summitpost-wa-mountains)))
