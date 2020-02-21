This directory holds data files for trip report classification project.

## Directory descriptions

-`trip-reports`/
    - csv files with headers "url", "title", "date", "text"
    - produced by `get-data.scrape-trs.core/get-trip-reports!`
- `labelled-trip-reports`/
    - csv files with headers "label", "title", "text"
    - produced from files in `trip-reports` manually
- `corpus`/
    - csv files with headers "document-label", "document-text"
    - produced from files in `labelled-trip-reports` by `get-data.create-corpus/create-corpus!`
- `datasets`/
    - .dataset files which are serialized clojure maps
    - produced from files in `corpus` by `analyze-data.core/create-dataset!`
