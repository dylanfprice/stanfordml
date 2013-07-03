(ns stanfordml.utils)

(defmacro dbg[x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))

(defn- scale [x y]
  (if (or (zero? x) (zero? y))
    1
    (Math/abs x)))

(defn float=
  ([x y] (float= x y 0.00001))
  ([x y epsilon] (<= (Math/abs (- x y))
                     (* (scale x y) epsilon))))


