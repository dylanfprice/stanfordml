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
             <a href='http://some-header-url.com'>
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
             <a href='{url}'>
             </a>
           </td>
           <td class='{class}'>
             {region}
           </td>
           <td class='{class}'>
             <a href='http://some-user-url.com'>
             </a>
           </td>
         </tr>
       </tbody>
     </table>
     </td></tr></tbody>
   </table>")

(defn make-test-page
  [url & {:keys [css-class region]
          :or {css-class "alt-1" region "Alpine Lakes"}}]
  (-> page-template
      (string/replace "{url}" url)
      (string/replace "{class}" css-class)
      (string/replace "{region}" region)))

(deftest extract-tr-urls-test
  (let [page (make-test-page "http://example.com/trip-reports/1234")]
    (is (= ["http://example.com/trip-reports/1234"]
           (test-ns/extract-tr-urls page))
        "extracts only trip report urls"))
  (let [page (make-test-page "http://example.com/trip-reports/1234"
                             :css-class "alt-2")]
    (is (= ["http://example.com/trip-reports/1234"]
           (test-ns/extract-tr-urls page))
        "extracts trip report urls from alt-2 table rows"))
  (let [page (make-test-page "http://example.com/trip-reports/1234"
                             :region "California")]
    (is (= []
           (test-ns/extract-tr-urls page))
        "ignores trip reports outside of Washington")))
