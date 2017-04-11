(ns analyze-data.gzip
  (:require '[clojure.java.io :as io])
  (:import java.util.zip GZIPInputStream GZIPOutputStream))

(defn gzip-reader
  "Like io/reader but reads input that is compressed with gzip."
  [x]
  (-> x io/input-stream GZIPInputStream. io/reader))

(defn gzip-writer
  "Like io/writer but writes output compressed with gzip."
  [x]
  (-> x io/output-stream GZIPOutputStream. io/writer))
