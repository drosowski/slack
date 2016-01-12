(ns slack.slackapi-test
  (:require [clojure.test :refer :all]
            [http.async.client :as http]
            [slack.slackapi :refer :all]))

(deftest a-test
  (testing "should handle slack error"
    (with-redefs [http/string (fn [resp] "{ \"error\": \"errd\"}")]
      (is (thrown-with-msg? java.io.IOException #"errd" (rtmStart "invalidToken"))))
  )
  (testing "should return websocket url on successful auth"
    (with-redefs [http/string (fn [resp] "{ \"url\": \"theurl\"}")]
      (is (= "theurl" (rtmStart "token"))))
  )
)
