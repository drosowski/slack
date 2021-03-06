(ns slack.slackapi-test
  (:require [clojure.test :refer :all]
            [http.async.client :as http]
            [http.async.client.websocket :as ws]
            (clj-json [core :as json])
            [slack.slackapi :refer :all]))

(deftype MyActions []
  SlackActions
  (handle-hello [self client] "hello")
  (handle-msg [self msg] "msg"))

(deftest a-test
  (testing "should handle slack error"
    (with-redefs [http/string (fn [resp] "{ \"error\": \"errd\"}")]
      (is (thrown-with-msg? java.io.IOException #"errd" (rtmStart "invalidToken"))))
  )
  (testing "should return websocket url on successful auth"
    (with-redefs [http/string (fn [resp] "{ \"url\": \"theurl\"}")]
      (is (= "theurl" (rtmStart "token"))))
  )
  (testing "should call protocol functions for slack api events"
    (is (= "hello" (handle-text nil "{ \"type\": \"hello\"}" (MyActions.))))
    (is (= "msg" (handle-text nil "{ \"type\": \"message\"}" (MyActions.))))
  )
)
