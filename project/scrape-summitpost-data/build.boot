; vi: ft=clojure

(set-env!
  :source-paths #{"src", "test"}
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [org.clojure/data.csv "0.1.3"]
                  [org.clojure/tools.cli "0.3.5"]
                  [reaver "0.1.2"]
                  [metosin/boot-alt-test "0.3.1" :scope "test"]])

(require '[metosin.boot-alt-test :refer [alt-test]])

(task-options!
    aot {:all true}
    pom {:project 'scrape-summitpost-data
         :version "0.0.0"}
    jar {:main 'scrape-summitpost-data.core})

(deftask build
  "Build scrape-summitpost-data uberjar."
  []
  (comp (aot) (pom) (uber) (jar) (target)))
