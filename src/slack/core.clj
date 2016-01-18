(ns slack.core
  (:require (clj-json [core :as json]))
  (:require [http.async.client.websocket :as websock])
  (:require [slack.slackapi :as api])
  (:require [slack.quotes :as quotes])
  (:gen-class))

(deftype MyActions []
  api/SlackActions
  (handle-hello [self client]
    (websock/send client :text "{\"id\": 1, \"type\": \"message\", \"channel\": \"G0GPBLL8M\", \"text\": \"foobaring\"}"))
  (handle-msg [self client msg] (println msg))
)

(defn -main
  [& args]
  (api/connect (first args) (MyActions.))
)
