(ns slack.core
  (:require (clj-json [core :as json]))
  (:require [http.async.client.websocket :as websock])
  (:require [slack.slackapi :as api])
  (:require [slack.quotes :as quotes])
  (:gen-class))

(deftype MyActions []
  api/SlackActions
  (handle-hello [self client]
    (api/send-msg client "G0GPBLL8M" "This is quote bot speaking. Enter \"hitme quotebot!\" to be served!"))
  (handle-msg [self client msg] 
    (if (= "hitme quotebot!" (:text msg))
      (api/send-msg client "G0GPBLL8M" (:text (quotes/random-quote)))
      (println msg)))
)

(defn -main
  [& args]
  (api/connect (first args) (MyActions.))
)
