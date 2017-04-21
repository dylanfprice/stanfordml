(ns scrape-summitpost-data.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [scrape-summitpost-data.get-item-texts
             :refer [get-item-texts get-item-texts-with-children]])
  (:gen-class))

(def cli-options
  [["-f" "--file FILE" "File name for csv data"
    :default "data.csv"]
   [nil
    "--include-child-content"
    (str "For each result, also include the content of all pages listed under "
         "the 'Children' header in the left sidebar.")]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Scrape summitpost pages from a search results page and save their"
        "text content to a csv."
        ""
        "Usage: scrape-summitpost-data [options] link"
        ""
        "Options:"
        options-summary
        ""
        "link: link (relative to summitpost domain) to a summitpost search"
        "      results page"
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
  [file-name search-link include-child-content?]
  (with-open [out-file (io/writer file-name)]
    (let [get-texts-fn (if include-child-content?
                         get-item-texts-with-children
                         get-item-texts)]
    (csv/write-csv out-file
                   (cons ["document-url", "document-name", "document-text"]
                         (get-texts-fn search-link))))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    (save-summitpost-search-results! (:file options)
                                     (first arguments)
                                     (:include-child-content options))))
