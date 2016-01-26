(ns slack.core-test
  (:require [clojure.test :refer :all]
            [slack.core]
            [slack.slackapi :as api])
  (:import [slack.core MyActions]))

(deftest a-test
  (testing "should handle bot command correctly"
    (with-redefs [api/send-msg (fn [token chan text] "called")]
      (is (not (= "called" (api/handle-msg (MyActions. "token") {:text "#quote", :subtype "bot_message"}))))
      (is (not (= "called" (api/handle-msg (MyActions. "token") {:text "nope", :subtype "me_message"}))))
      (is (= "called" (api/handle-msg (MyActions. "token") {:text "#quote", :subtype "me_message"}))))
  )
)
