(ns slack.core
  (:require (clj-json [core :as json]))
  (:require [http.async.client.websocket :as websock])
  (:require [slack.slackapi :as api])
  (:require [slack.quotes :as quotes])
  (:gen-class))

(defn handle-hello [ws]
  (println "hello")
  (websock/send ws :text "{\"id\": 1, \"type\": \"message\", \"channel\": \"G0GPBLL8M\", \"text\": \"foobaring\"}")
)

(defn handle-message [ws msg]
  (println msg)
)

(defn -main
  [& args]
  (api/connect (first args) {
    :handle-hello handle-hello
    :handle-message handle-message
    })
)
