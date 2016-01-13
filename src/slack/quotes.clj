(ns slack.quotes
  (:require (clj-json [core :as json]))
  (:require [clojure.java.io :as io])
  (:gen-class))


(defn read-file [filename]
  (slurp 
     (io/file (io/resource filename)))
)

(defn parse-quotes [filename]
  (json/parse-string (read-file filename) true)
)

(def cached-quotes (memoize parse-quotes))

(defn random-quote [&]
  (let [quotes (cached-quotes "se_quotes.txt")]
    (get quotes (rand-int (- (count quotes) 1))))
) 
