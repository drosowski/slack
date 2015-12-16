(ns slack.core
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
      (do
        (println "Slack error: " error)
        (System/exit 1))
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

(defn -main
  [& args]
  (let [url (rtmStart (first args))]
  (println "Connecting...")
  (with-open [client (http/create-client)]
    (let [ws (http/websocket client
                             url
                             :text handle-message)]
      (loop [] (recur)))))
)
