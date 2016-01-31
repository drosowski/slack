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
    (println "Authenticating to slack...")
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
  (handle-msg [self msg] "Incoming message")
)

(defn handle-text [client rawmsg slack-actions]
  (let [msg (respToMap rawmsg)]
    (println "IN: " msg)
    (cond
      (= "hello" (:type msg)) (handle-hello slack-actions client)
      (= "message" (:type msg)) (handle-msg slack-actions msg)))
)
 
(def msg-id (atom 1))
(defn ping [client]
  (proxy [java.util.TimerTask] []
    (run []
      (let [output {:id (swap! msg-id inc), :type "ping"}]
        (println "PING: " output)
        (websock/send client :text output))))
)


(defn connect [apikey slack-actions]
  (try
    (let [url (rtmStart apikey)]
      (println "Connecting...")
      (with-open [client (http/create-client)]
        (let [ws (http/websocket client
                                 url
                                 :text #(handle-text %1 %2 slack-actions))]
          (doto (new java.util.Timer true) (.schedule (ping client) 0 10000))
          (loop [] 
            (Thread/sleep 500)
            (recur)))))
    (catch java.io.IOException ex
      (do
        (println (.getMessage ex))
        (System/exit 1))
    )
  )
)

(defn send-msg [token channel msg]
  (with-open [client (http/create-client)]
    (let [resp (http/POST client "https://slack.com/api/chat.postMessage" :body 
      {:token token, 
       :channel channel, 
       :username (System/getenv "SLACK_NAME"), 
       :icon_emoji (System/getenv "SLACK_EMOJI"), 
       :text msg})]
      (-> resp
        http/await
        http/string)))
)


