(ns scrape-trs.cascade-climbers.extract-pager-urls-test
  (:require [clojure.test :refer [deftest is]]
            [scrape-trs.cascade-climbers.extract-pager-urls :as test-ns]))

(deftest extract-pager-urls-test
  (let [page
        "<table class='t_standard pagination'>
           <tbody>
             <tr>
               <td class='tdheader'>Page 1 of 338</td>
               <td class='alt-1'>1</td>
               <td class='alt-1'><a href='/sb/13/sk/0/page/2'>2</a></td>
               <td class='alt-1'><a href='/sb/13/sk/0/page/3'>3</a></td>
               <td class='alt-1'><a href='/sb/13/sk/0/page/338'>338</a></td>
               <td class='alt-1'><a href='/sb/13/sk/0/page/2'>&gt;</a></td>
               <td class='tdheader'></td>
             </tr>
           </tbody>
         </table>"]
    (is (= ["/sb/13/sk/0/page/2"
            "/sb/13/sk/0/page/3"
            "/sb/13/sk/0/page/338"
            "/sb/13/sk/0/page/2"]
           (#'test-ns/extract-pager-urls page))
        "extracts pager urls"))
  (let [page "<table></table>"]
    (is (= [] (#'test-ns/extract-pager-urls page))
        "returns empty sequence when there are no pager urls")))

(deftest extract-last-page-test
  (is (= 1
         (#'test-ns/extract-last-page ["/sb/13/sk/0/page/1"]))
      "extracts value of page path in url")
  (is (= 3
         (#'test-ns/extract-last-page ["/sb/13/sk/0/page/1"
                                       "/sb/13/sk/0/page/2"
                                       "/sb/13/sk/0/page/3"]))
      "extracts highest value of page param")
  (is (thrown? java.lang.NumberFormatException
         (#'test-ns/extract-last-page ["/sb/13/sk/0/page/f"]))
      "fails on a non-integer value of page param"))

(deftest extract-all-pager-urls-test
  (let [page "<table></table>"]
    (is (= nil
           (test-ns/extract-all-pager-urls page))
        "returns nil when there are no pager urls"))
  (let [page
        "<table class='t_standard pagination'>
           <tbody>
             <tr>
               <td class='tdheader'>Page 1 of 6
               <td class='alt-1'>1</td>
               <td class='alt-1'><a href='/sb/13/sk/0/page/2'>2</a></td>
               <td class='alt-1'><a href='/sb/13/sk/0/page/5'>338</a></td>
               <td class='alt-1'><a href='/sb/13/sk/0/page/2'>&gt;</a></td>
               <td class='tdheader'></td>
             </tr>
           </tbody>
         </table>"]
    (is (= ["/sb/13/sk/0/page/1", "/sb/13/sk/0/page/2"
            "/sb/13/sk/0/page/3", "/sb/13/sk/0/page/4"
            "/sb/13/sk/0/page/5"]
           (test-ns/extract-all-pager-urls page))
        "extracts entire range of urls")))
