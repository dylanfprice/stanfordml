(ns analyze-data.score-model)

(defn true-positives
  "Returns the number of true positives of a class or sum of true positives
  for all classes. A true positive for class c is a sample that is labelled c
  and predicted as c."
  ([confusion-matrix]
   (->> confusion-matrix
        (keys)
        (map (partial true-positives confusion-matrix))
        (reduce +)))
  ([confusion-matrix c]
   (let [labelled-c-and-predicted-c (get-in confusion-matrix [c c] 0)]
     labelled-c-and-predicted-c)))

(defn false-positives
  "Returns the number of false positives of a class or sum of false positives
  for all classes. A false positive for class c is a sample that is not
  labelled c but predicted as c."
  ([confusion-matrix]
   (->> confusion-matrix
        (keys)
        (map (partial false-positives confusion-matrix))
        (reduce +)))
  ([confusion-matrix c]
   (let [labelled-not-c (dissoc confusion-matrix c)
         labelled-not-c-but-predicted-c (map #(get % c 0)
                                             (vals labelled-not-c))]
     (reduce + labelled-not-c-but-predicted-c))))

(defn false-negatives
  "Returns the number of false negatives of a class or sum of false negatives
  for all classes. A false negative for class c is a sample that is labelled c
  but not predicted as c."
  ([confusion-matrix]
   (->> confusion-matrix
        (keys)
        (map (partial false-negatives confusion-matrix))
        (reduce +)))
  ([confusion-matrix c]
   (let [labelled-c (confusion-matrix c)
         labelled-c-but-predicted-not-c (dissoc labelled-c c)]
     (reduce + (vals labelled-c-but-predicted-not-c)))))

(defn true-negatives
  "Returns the number of true negatives of a class or sum of true negatives
  for all classes. A true negative for class c is a sample that is not
  labelled c and not predicted as c."
  ([confusion-matrix]
   (->> confusion-matrix
        (keys)
        (map (partial true-negatives confusion-matrix))
        (reduce +)))
  ([confusion-matrix c]
   (let [labelled-not-c (dissoc confusion-matrix c)
         labelled-not-c-and-predicted-not-c (map #(dissoc % c)
                                                 (vals labelled-not-c))]
     (reduce + (mapcat vals labelled-not-c-and-predicted-not-c)))))

(defn precision
  "Return (# correctly predicted as c / # predicted as c)."
  [confusion-matrix c]
  (let [tp (true-positives confusion-matrix c)
        fp (false-positives confusion-matrix c)]
    (if (zero? (+ tp fp))
      0
      (/ tp (+ tp fp)))))

(defn recall
  "Return (# correctly predicted as c / # labelled c)."
  [confusion-matrix c]
  (let [tp (true-positives confusion-matrix c)
        fn (false-negatives confusion-matrix c)]
    (if (zero? (+ tp fn))
      0
      (/ tp (+ tp fn)))))

(defn specificity
  "Return (# correctly predicted as not c / # labelled not c)."
  [confusion-matrix c]
  (let [tn (true-negatives confusion-matrix c)
        fp (false-positives confusion-matrix c)]
    (if (zero? (+ tn fp))
      0
      (/ tn (+ tn fp)))))

(defn accuracy
  "Return (# correctly predicted / # samples)."
  [confusion-matrix]
  (let [tp (true-positives confusion-matrix)
        num-samples (->> confusion-matrix
                         (vals)
                         (mapcat vals)
                         (reduce +))]
    (/ tp num-samples)))

(defn f1-score
  "Calculates the F1 score of a confusion matrix, which is a weighted average
  of the precision and recall."
  [confusion-matrix c]
  (let [prec (precision confusion-matrix c)
        rec (recall confusion-matrix c)]
    (if (zero? (+ prec rec))
      0
      (* 2 (/ (*  prec rec) (+ prec rec))))))
