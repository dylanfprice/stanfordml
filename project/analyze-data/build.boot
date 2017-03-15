; vi: ft=clojure

(set-env!
  :resource-paths #{"src", "test"}
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [org.clojure/data.csv "0.1.3"]
                  [incanter "1.9.0"]
                  [adzerk/boot-test "1.2.0" :scope "test"]])

(require '[adzerk.boot-test :refer [test]])

(task-options!
    aot {:all true}
    pom {:project 'tf-idf
         :version "0.0.0"}
    jar {:main 'analyze-data.core})

(deftask build []
  (comp (aot) (pom) (uber) (jar) (target)))
