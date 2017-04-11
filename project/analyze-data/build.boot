; vi: ft=clojure

(set-env!
  :source-paths #{"src", "test"}
  :resource-paths #{"resources"}
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [org.clojure/data.csv "0.1.3"]
                  [metosin/boot-alt-test "0.3.1" :scope "test"]
                  [net.mikera/core.matrix "0.58.0"]
                  [net.mikera/vectorz-clj "0.46.0"]])

(require '[metosin.boot-alt-test :refer [alt-test]])

(task-options!
    aot {:all true}
    pom {:project 'tf-idf
         :version "0.0.0"}
    jar {:main 'analyze-data.core})

(deftask build []
  (comp (aot) (pom) (uber) (jar) (target)))
