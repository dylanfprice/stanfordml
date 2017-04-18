(ns scrape-summitpost-data.extract-from-item
  (:require [clojure.string :as string]
            [reaver]))

(defn extract-item-name
  "Given the base url of summitpost a url to an item (such as a mountain),
  return the name of the item."
  [base-url url]
  (nth (re-find (re-pattern (str base-url "/([^/]+)/.*")) url)
       1))

(defn extract-item-text
  "Given a string containing an item page from summitpost, return the text of
  the main article."
  [page]
  (reaver/extract (reaver/parse page) [] "article" reaver/text))

(defn- select-left-box-elements
  [node]
  (reaver/select node "#left_box div[class^=\"left_box\"]"))

(defn- extract-left-box-heading
  [node]
  (reaver/extract node [] ".left_box_heading" reaver/text))

(defn- is-children-heading?
  [node]
  (let [heading (or (extract-left-box-heading node) "")]
    (string/starts-with? heading "Children")))

(defn- extract-left-box-link
  [node]
  (reaver/extract
    node
    []
    ".left_box_list_angle_bluegray > a" (reaver/attr :href)))

(defn extract-children-links
  "Given a string containing an item page from summitpost, return links to all
  pages under the 'Children' header in the left sidebar."
  [page]
  (->> (reaver/parse page)
       (select-left-box-elements)
       (drop-while #(not (is-children-heading? %)))
       (drop 1)
       (take-while #(nil? (extract-left-box-heading %)))
       (keep extract-left-box-link)))

