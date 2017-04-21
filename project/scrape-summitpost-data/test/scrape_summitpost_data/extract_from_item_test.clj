(ns scrape-summitpost-data.extract-from-item-test
  (:require [clojure.test :refer [deftest is]]
            [reaver]
            [scrape-summitpost-data.extract-from-item :as test-ns]))

(deftest extract-item-name-test
  (is (= "test-item-name"
         (test-ns/extract-item-name "http://example.org" "http://example.org/test-item-name/12345"))
      "extracts item name from page"))

(deftest extract-item-text-test
  (is (= "content"
         (test-ns/extract-item-text "<html>
                                       <body>
                                         <article>content</article>
                                       </body>
                                     </html>"))))

(deftest extract-left-box-heading-test
  (let [node (reaver/parse "<div class='left_box_heading'>heading</div>")]
    (is (= "heading"
           (#'test-ns/extract-left-box-heading node)))))

(deftest is-children-heading?
  (let [node (reaver/parse "<div class='left_box_heading'>heading</div>")]
    (is (false? (#'test-ns/is-children-heading? node))))
  (let [node (reaver/parse "<div class='left_box_heading'>Children</div>")]
    (is (true? (#'test-ns/is-children-heading? node)))))

(deftest extract-left-box-link
  (let [node (reaver/parse
               "<div class='left_box_list_angle_bluegray'>
                  <a href='http://www.summitpost.org/item/123'>
                    Item
                  </a>
                </div>")]
    (is (= "http://www.summitpost.org/item/123"
           (#'test-ns/extract-left-box-link node)))))

(def left-box-test-html
  "<div id='left_box'>

   <div class='left_box_heading_link'>
       Images
   </div>
   <div class='left_box_heading_link'>
     <a href='http://www.summitpost.org/item/climbers-log/123'>
       Climber's Log
     </a>
   </div>

   <div class='left_box_heading'>Children</div>
   <div class='left_box_heading_link'>
     <a href='http://www.summitpost.org/item/routes/123'>
       Routes
     </a>
   </div>
   <div class='left_box_list_angle_bluegray'>
     <a href='http://www.summitpost.org/route1/123'>
       Route 1
     </a>
   </div>
   <div class='left_box_list_angle_bluegray'>
     <a href='http://www.summitpost.org/route2/123'>
       Route 2
     </a>
   </div>

   <div class='left_box_heading'>
     Geography
   </div>

   </div>")

(deftest extract-children-links-test
  (is (= ["http://www.summitpost.org/route1/123"
          "http://www.summitpost.org/route2/123"]
         (test-ns/extract-children-links left-box-test-html))
      "extracts links to other pages under 'Children' header"))
