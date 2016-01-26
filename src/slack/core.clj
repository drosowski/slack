(ns slack.core
  (:require (clj-json [core :as json]))
  (:require [http.async.client.websocket :as websock])
  (:require [slack.slackapi :as api])
  (:require [slack.quotes :as quotes])
  (:gen-class))

(deftype MyActions [token]
  api/SlackActions
  (handle-hello [self client]
    (api/send-msg token (System/getenv "SLACK_CHANNEL") "This is quote bot speaking. Type \"#quote\" to be served!"))
  (handle-msg [self msg] 
    (if (and (= "#quote" (:text msg)) (not (= "bot_message" (:subtype msg))))
      (let [quote (quotes/random-quote) channel (System/getenv "SLACK_CHANNEL")]
        (api/send-msg token channel (:text quote))
        (api/send-msg token channel (str "-- " (:author quote))))
      nil))
)

(defn -main
  [& args]
  (let [token (System/getenv "SLACK_TOKEN")]
    (api/connect token (MyActions. token)))
)
