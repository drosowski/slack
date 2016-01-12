(ns slack.core
  (:require (clj-json [core :as json]))
  (:require [slack.slackapi :as api])
  (:require [slack.quotes :as quotes])
  (:gen-class))


(defn -main
  [& args]
  (api/connect (first args))
)
