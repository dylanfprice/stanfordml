(ns scrape-summitpost-data.extract-from-item-test
  (:require [clojure.test :refer [deftest is]]
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
