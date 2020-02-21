(ns scrape-trs.cascade-climbers.extract-tr-urls-test
  (:require [clojure.string :as string]
            [clojure.test :refer [deftest is]]
            [scrape-trs.cascade-climbers.extract-tr-urls :as test-ns]))

(def page-template
  "<table class='t_outer'></table>
   <table class='t_outer'></table>
   <table class='t_outer'></table>
   <table class='t_outer'></table>
   <table class='t_outer'></table>
   <table class='t_outer'></table>
   <table class='t_outer'></table>
   <table class='t_outer'></table>
   <table class='t_outer'></table>
   <table class='t_outer'>
     <tbody><tr><td>
     <table>
       <tbody>
         <tr>
           <td class='tdheader'>
             <a href='/some-header-link'>
             Date
             </a>
           </td>
           <!-- other headers here -->
         </tr>
         <tr>
           <td class='{class}'>
             4/23/2017
           </td>
           <td class='{class}'>
             Alpine
           </td>
           <td class='{class}'>
             <a href='{link}'>
             </a>
           </td>
           <td class='{class}'>
             Alpine Lakes
           </td>
           <td class='{class}'>
             <a href='/some-user-link'>
             </a>
           </td>
         </tr>
       </tbody>
     </table>
     </td></tr></tbody>
   </table>")

(defn make-test-page
  [link & {:keys [css-class]
          :or {css-class "alt-1"}}]
  (-> page-template
      (string/replace "{link}" link)
      (string/replace "{class}" css-class)))

(deftest extract-tr-urls-test
  (let [page (make-test-page "/trip-reports/1234")]
    (is (= ["http://example.com/trip-reports/1234"]
           (test-ns/extract-tr-urls "http://example.com" page))
        "extracts only trip report urls"))
  (let [page (make-test-page "/trip-reports/1234"
                             :css-class "alt-2")]
    (is (= ["http://example.com/trip-reports/1234"]
           (test-ns/extract-tr-urls "http://example.com" page))
        "extracts trip report urls from alt-2 table rows")))
