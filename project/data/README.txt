Holds data files for trip report classification project.

trip-reports/
  csv files with headers "url", "title", "date", "text"
  (note: uwcc files currently only have "title" and "text")
labelled-trip-reports/
  csv files with headers "label", "title", "text"
corpus/
  csv files with headers "document-label", "document-text"
datasets/
  .dataset files which are serialized clojure maps

trip-reports -> labelled-trip-reports
  manually

labelled-trip-reports -> corpus
  get-data.create-corpus/create-corpus!

corpus -> datasets
  analyze-data.core/create-dataset
