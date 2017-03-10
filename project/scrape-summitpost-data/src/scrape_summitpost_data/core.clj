(ns scrape-summitpost-data.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [scrape-summitpost-data.get-item-texts :refer [get-item-texts]])
  (:gen-class))

(def cli-options
  [["-f" "--file FILE" "File name for csv data"
    :default "data.csv"]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Scrape summitpost pages from a search results page and save their
        text content to a csv."
        ""
        "Usage: scrape-summitpost-data [options] link"
        ""
        "Options:"
        options-summary
        ""
        "link: link (i.e. relative to summitpost domain) to a summitpost search results page"
        "      e.g. /object_list.php?object_type=1&state_province_1=Washington&sort_select_1=object_name"
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn save-summitpost-search-results!
  "Save a csv of item names and page texts for all search result items found
  at `search-link`."
  [file-name search-link]
  (with-open [out-file (io/writer file-name)]
    (csv/write-csv out-file
                   (cons ["item-name", "item-text"]
                         (get-item-texts search-link)))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    (save-summitpost-search-results! (:file options) (first arguments))))
