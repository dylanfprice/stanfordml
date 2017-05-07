(ns scrape-trs.protocol
  (:import java.text.SimpleDateFormat))

(defrecord TripReport [title date text])

(def trip-report-date-format (SimpleDateFormat. "yyyy-MM-dd"))

(defprotocol ScrapeTripReport
  ""
  (extract-pager-urls
    [this list-page]
    "Given a page (as a string) that lists links to trip reports and may have
    pagination, return a sequence of urls to all pages in the pagination, or a
    one item sequence containing list-page if there is no pagination.")
  (extract-trip-report-urls
    [this list-page]
    "Given a page (as a string) that lists links to trip reports, return a
    sequence of urls to all trip reports listed on the page.")
  (extract-trip-report
    [this trip-report-page]
    "Given a page (as a string) that contains a trip report, return a
    TripReport record.") )

