; vi: ft=clojure

(set-env!
  :source-paths #{"src", "test"}
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [reaver "0.1.2"]
                  [metosin/boot-alt-test "0.3.1" :scope "test"]])

(require '[metosin.boot-alt-test :refer [alt-test]])

(task-options!
    aot {:all true}
    pom {:project 'scrape-trs
         :version "0.0.0"}
    jar {:main 'scrape-trs.core})

(deftask build
  "Build scrape-trs uberjar."
  []
  (comp (aot) (pom) (uber) (jar) (target)))
