Classifying trip report text using k-nearest neighbors and naive bayes.

## Results

See [results](results/).

## Running code and tests

```bash
cd code/
clj -A:repl -i
```

```bash
cd code/
clj -A:test
```

## The data

This directory holds data files for trip report classification project.

- `trip-reports`/
    - csv files with headers "url", "title", "date", "text"
    - produced by `get-data.scrape-trs.core/get-trip-reports!`
- `labelled-trip-reports`/
    - csv files with headers "label", "title", "text"
    - produced from files in `trip-reports` manually
- `datasets`/
    - csv files with headers "document-label", "document-text"
        - produced from files in `labelled-trip-reports` by `create-corpus.core/create-corpus!`
    - .dataset files which are serialized clojure maps
        - produced from files in same directory by `analyze-data.core/create-dataset!`

## An example pipeline

```clojure
(require '[scrape-trs.core :refer [get-trip-reports!]]
         '[create-corpus.core :refer [create-corpus!]]
         '[analyze-data.core :refer [create-dataset! evaluate-dataset!]]) 

(def summitpost-rainier-trs "https://www.summitpost.org/object_list.php?object_type=5&search_select_5=name_only&map_5=0&contributor_id=&order_type_5=DESC&object_name_5=rainier&state_province_5=Washington&order_type_5=DESC")

(get-trip-reports! summitpost-rainier-trs "../data/trip-reports/summitpost-rainier.csv")
; label some trip reports, see [The data](#the-data)
(create-corpus! "../data/labelled-trip-reports/summitpost-rainier.csv" "../data/datasets/summitpost-rainier.csv" #{"label1", "label2"})

(create-dataset! "../data/datasets/summitpost-rainier.csv")
(evaluate-dataset! "../data/datasets/summitpost-rainier.dataset" :knn)
(evaluate-dataset! "../data/datasets/summitpost-rainier.dataset" :naive-bayes)
```
