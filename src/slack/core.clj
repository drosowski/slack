(ns slack.core
  (:require (clj-json [core :as json]))
  (:require [http.async.client.websocket :as websock])
  (:require [slack.slackapi :as api])
  (:require [slack.quotes :as quotes])
  (:gen-class))

(deftype MyActions [token]
  api/SlackActions
  (handle-hello [self client]
    (api/send-msg token "G0GPBLL8M" "This is quote bot speaking. Type \"#quote\" to be served!"))
  (handle-msg [self msg] 
    (if (= "#quote" (:text msg))
      (let [quote (quotes/random-quote)]
        (api/send-msg token "G0GPBLL8M" (:text quote))
        (api/send-msg token "G0GPBLL8M" (str "-- " (:author quote))))
      nil))
)

(defn -main
  [& args]
  (let [token (first args)]
    (api/connect token (MyActions. token)))
)
