(ns scrape-summitpost-data.get-item-texts-test
  (:require [clojure.test :refer [deftest is]]
            [scrape-summitpost-data.get-item-texts :as test-ns]))

(deftest make-result-entry-test
  (is (= ["http://www.summitpost.org/item-name/1234" "item-name" "text"]
         (#'test-ns/make-result-entry
           "http://www.summitpost.org/item-name/1234"
           "text"))
      "returns a [result-url, result-name, result-text]"))

(deftest get-item-texts-test
  (with-redefs [test-ns/get-result-urls
                (fn [search-link]
                  ["http://www.summitpost.org/item-name/1234"])
                slurp (fn [f] "<article>text</article>")]
    (is (= [["http://www.summitpost.org/item-name/1234" "item-name" "text"]]
           (test-ns/get-item-texts "/search-link"))
        "returns sequence of [result-url, result-name, result-text]")))

(deftest get-result-text-and-child-texts-test
  (with-redefs [slurp (fn [f] "<article>child-text</article>")]
    (is (= "result\nchild-text\nchild-text"
           (#'test-ns/get-result-text-and-child-texts
             "<article>result</article>"
             ["url1" "url2"]))
        "concatenates result content with contents found at each child url")))
