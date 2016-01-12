(ns slack.slackapi
  (:require [http.async.client :as http])
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

(defn handle-message [ws rawmsg]
  (let [msg (respToMap rawmsg)]
    (println msg))
)

(defn connect [apikey]
  (try
    (let [url (rtmStart apikey)]
      (println "Connecting...")
      (with-open [client (http/create-client)]
        (let [ws (http/websocket client
                                 url
                                 :text handle-message)]
          (println "Connection established!")
          (loop [] (recur)))))
    (catch java.io.IOException ex
      (do
        (println (.getMessage ex))
        (System/exit 1))
    )
  )
)

