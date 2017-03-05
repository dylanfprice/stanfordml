; vi: ft=clojure

(set-env!
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [incanter "1.9.1"]])

(task-options!
    aot {:all true}
    pom {:project 'analyze-data
         :version "0.0.0"}
    jar {:main 'analyze-data.core})

(deftask build []
  (comp (aot) (pom) (uber) (jar) (target)))
