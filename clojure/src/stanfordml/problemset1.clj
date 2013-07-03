(ns stanfordml.problemset1
  (:require (incanter 
              [core :refer :all] 
              [io :refer :all]))
  (:require [stanfordml.utils :as utils :refer [dbg]]))
 

(defn weights [X x tau]
  "Calculates the vector of weights"
  (let [denom (* 2 (* tau tau))]
    (->> X
       (map (partial minus (trans x)))
       matrix
       abs
       (map sum)
       sq
       (mult (/ -1 denom))
       exp)))

(defn h-theta-bnr [X theta]
  "Computes h-theta for binary logistic regression"
  (->> (trans X)
       (mmult (mult -1 (trans theta)))
       exp
       (plus 1)
       (div 1)
       trans))

(defn z [y weights h-theta]
  (mult weights (minus y h-theta)))

(defn D [weights h-theta]
  (->> h-theta
       (minus 1)
       (mult -1 weights h-theta)
       to-list
       diag))

(defn H [X D lambda n]
  (minus (mmult (trans X) D X)
         (mult lambda (identity-matrix n))))

(defn grad-l-theta [X z lambda theta]
  (minus (mmult (trans X) z)
         (mult lambda theta)))

(defn delta-fn [lambda tau x X y theta]
  (let [n (ncol X) ;number of features
        w (weights X x tau)
        h-theta (h-theta-bnr X theta)] 
    (mmult
      (solve (H X (D w h-theta) lambda n))
      (grad-l-theta X (z y w h-theta) lambda theta))))


(defn newtons-method [X y delta-fn
                      & {:keys [converge-delta] 
                         :or {converge-delta 0.0001}}]
  "Runs Newton's Method 
   X:              design matrix (matrix of training inputs)
   y:              matrix of training targets
   delta-fn:       fn which given X, y, and theta (matrix of parameters),
                   returns the delta for the thetas. This is the inverse of the
                   hessian times the gradient of the log-likelihood fn.
   converge-delta: when the change in thetas over an iteration drops below this,
                   the algorithm stops
   
   returns the final value of theta"

  (loop [;initialize old-thetas so we don't converge first pass
         old-theta (matrix converge-delta (ncol X) 1)
         ;initialize all thetas to zero
         theta (matrix 0 (ncol X) 1)]
    (let [delta (abs (minus old-theta theta))]
      (if (every? true?
                (matrix-map (partial > converge-delta) delta))
        theta
        (recur theta (minus theta (delta-fn X y theta)))))))


(defn lwlr [X y x tau]
  "Locally weighted logistic regression"
  (let [min-delta 0.0001
        lambda 0.0001
        ; number of training examples
        m (nrow X)
        ; number of features
        n (ncol X)
        ; X_0 = 1 for all training examples
        X (bind-columns (matrix 1 m 1) X)
        ; x_0 = 1
        x (bind-rows (matrix [[1]]) x)
        theta (newtons-method X 
                              y 
                              (partial delta-fn lambda tau x) 
                              :converge-delta 0.0001)]
    (->> (mmult (mult -1 (trans theta)) x)
         exp
         (plus 1)
         (div 1)
         first
         (< 0.5))))


(defn load-data []
  (let [X (to-matrix (read-dataset "../problemset1/q2/data/x.dat" :delim \space))
        y (to-matrix (read-dataset "../problemset1/q2/data/y.dat" :delim \space))]
    [X y]))


(defn run-lwlr [x-list tau]
  (let [[X y] (load-data) 
        x (matrix x-list)]
   (lwlr X y x tau)))


(defn -main [& args]
  (let [[X y] (load-data)
        res 50
        tau 0.01]
    (run-lwlr [[0.16] [-0.92]] 0.01)))

