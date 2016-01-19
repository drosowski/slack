(ns slack.slackapi
  (:require [http.async.client :as http])
  (:require [http.async.client.websocket :as websock])
  (:require (clj-json [core :as json]))
  (:gen-class))


(defn respToMap [response]
  (into {} 
  (for [[k v] (json/parse-string response)] 
    [(keyword k) v]))
)

(defn webSockUrl [response]
  (let [error (response :error)]
    (if error
      (throw (java.io.IOException. (str "Slack error: " error)))
      (response :url)))
)

(defn rtmStart [token]
  (with-open [client (http/create-client)]
    (println "Authenticating to slack using token:" token)
    (let [response (http/GET client (str "https://slack.com/api/rtm.start?token=" token))]
      (-> response
        http/await
        http/string
	respToMap
	webSockUrl)))
)

(defprotocol SlackActions
  "protocol defining what to do on various slack API events"
  (handle-hello [self client] "Initial connect to slack websocket successful")
  (handle-msg [self client msg] "Incoming message")
)

(defn handle-text [client rawmsg slack-actions]
  (let [msg (respToMap rawmsg)]
    (cond
      (= "hello" (:type msg)) (handle-hello slack-actions client)
      (= "message" (:type msg)) (handle-msg slack-actions client msg)
      :else (println "Unknown: " rawmsg)))
)
 

(defn connect [apikey slack-actions]
  (try
    (let [url (rtmStart apikey)]
      (println "Connecting...")
      (with-open [client (http/create-client)]
        (let [ws (http/websocket client
                                 url
                                 :text #(handle-text %1 %2 slack-actions))]
          (loop [] (recur)))))
    (catch java.io.IOException ex
      (do
        (println (.getMessage ex))
        (System/exit 1))
    )
  )
)

(def msg-id (atom 0))
(defn send-msg [client channel msg]
  (websock/send client :text (json/generate-string {:id (str (swap! msg-id inc)), :type "message", :channel channel, :text msg}))
)

