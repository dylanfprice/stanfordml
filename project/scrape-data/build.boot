; vi: ft=clojure

(set-env!
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.8.0"]])

(task-options!
    aot {:all true}
    pom {:project 'scrape-data
         :version "0.0.0"}
    jar {:main 'scrape-data.core})

(deftask build []
  (comp (aot) (pom) (uber) (jar) (target)))
