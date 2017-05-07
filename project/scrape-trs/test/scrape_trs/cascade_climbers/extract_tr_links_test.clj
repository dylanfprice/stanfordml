(ns scrape-trs.cascade-climbers.extract-tr-links-test
  (:require [clojure.test :refer [deftest is]]
            [scrape-trs.cascade-climbers.extract-tr-links :as test-ns]))

(def page
  "<table class='t_inner'>
     <tbody>
       <tr>
         <td>
           <a href='http://cascadeclimbers.com/forum/ubbthreads.php?ubb=tripreports&sb=2&page=1&amp;sk=0'>
           Date
           </a>
         </td>
         <td>
           <a href='http://cascadeclimbers.com/forum/ubbthreads.php?ubb=tripreports&amp;sb=3&amp;page=1&amp;sk=0'>
           Type
           </a>
         </td>
         <td>
           <a href='http://cascadeclimbers.com/forum/ubbthreads.php?ubb=tripreports&sb=5&page=1&sk=0'>
           Location|Route
           </a>
         </td>
       </tr>

       <tr>
         <td class='alt-1'>
           4/23/2017
         </td>
         <td class='alt-1'>
           Alpine
         </td>
         <td class='alt-1'>
           <a href='http://cascadeclimbers.com/forum/ubbthreads.php?ubb=showflat&amp;Number=1154575'>
           </a>
         </td>
       </tr>

       <tr>
         <td class='alt-2'>
           12/13/2016
         </td>
         <td class='alt-2'>
           Alpine
         </td>
         <td class='alt-2'>
           <a href='http://cascadeclimbers.com/forum/ubbthreads.php?ubb=showflat&Number=1154544'>
           </a>
         </td>
       </tr>
     </tbody>
   </table>")

(deftest extract-tr-links-test
  (is (= ["http://cascadeclimbers.com/forum/ubbthreads.php?ubb=showflat&Number=1154575"
          "http://cascadeclimbers.com/forum/ubbthreads.php?ubb=showflat&Number=1154544"]
         (test-ns/extract-tr-links page))))
