(ns scrape-trs.summitpost.core
  (:require [scrape-trs.summitpost
             [extract-pager-urls :refer [extract-all-pager-urls]]
             [extract-result-urls :refer [extract-result-urls]]
             [extract-trip-report :refer [extract-trip-report]]])
  (:import scrape_trs.protocol.ScrapeTripReport))

(def base-url "https://www.summitpost.org")
(def usage-notes
  (str "List pages should be from the Summitpost trip reports search and be "
       "the list view not the map view. E.g. https://www.summitpost.org/"
       "object_list.php?object_type=5&search_select_5=name_only"
       "&contributor_id=&order_type_5=DESC&object_name_5=rainier"
       "&state_province_5=Washington&order_type_5=DESC"))

(deftype SPScrapeTripReport []

  ScrapeTripReport

  (extract-pager-urls
    [this list-page]
    (extract-all-pager-urls base-url list-page))

  (extract-trip-report-urls
    [this list-page]
    (extract-result-urls base-url list-page))

  (extract-trip-report
    [this trip-report-url trip-report-page]
    (extract-trip-report trip-report-url trip-report-page)))
