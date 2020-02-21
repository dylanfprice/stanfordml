(ns scrape-trs.summitpost.extract-trip-report-test
  (:require [clojure.string :as string]
            [clojure.test :refer [deftest is]]
            [reaver]
            [scrape-trs.protocol :refer [map->TripReport]]
            [scrape-trs.summitpost.extract-trip-report :as test-ns]))

(def ^:private test-page-template
  "<html>
     <body>
       <h1 class='adventure-title'>{title}</h1>
       <table class='object-properties-table'>
         <tr>
           <th>
             <img title='Date'>
             Date Climbed/Hiked:
           </th>
           <td>{date}</td>
         </tr>
       </table>
       <div class='full-content'>{text}</div>
     </body>
   </html>")

(defn- make-test-page
  [& {:keys [title date text]
      :or {title ""
           date ""
           text ""}}]
  (-> test-page-template
      (string/replace "{title}" title)
      (string/replace "{date}" date)
      (string/replace "{text}" text)))

(deftest extract-item-title-test
  (is (= "Test Title"
         (#'test-ns/extract-item-title
          (reaver/parse (make-test-page :title "Test Title"))))))

(deftest extract-item-date-test
  (is (= "2000-01-01"
         (#'test-ns/extract-item-date
          (reaver/parse (make-test-page :date "Jan 1, 2000")))))
  (is (= nil
         (#'test-ns/extract-item-date
          (reaver/parse (make-test-page :date ""))))))

(deftest extract-item-text-test
  (is (= "content"
         (#'test-ns/extract-item-text
          (reaver/parse (make-test-page :text "content"))))))

(deftest extract-trip-report-test
  (is (= (map->TripReport
          {:url "http://example.com/test-tr"
           :title "Test Title"
           :date "2000-01-01"
           :text "content"})
         (test-ns/extract-trip-report "http://example.com/test-tr"
                                      (make-test-page :title "Test Title"
                                                      :date "Jan 1, 2000"
                                                      :text "content")))))
