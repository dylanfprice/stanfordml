(ns scrape-trs.ensure-sequence)

(defn ensure-sequence
  "If arg is sequential?, return arg. Otherwise return a one-element vector
  containing arg."
  [arg]
  (if (sequential? arg) arg [arg]))
