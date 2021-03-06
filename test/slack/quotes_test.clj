(ns slack.quotes-test
  (:require [clojure.test :refer :all]
            [slack.quotes :refer :all]))

(deftest a-test
  (testing "should parse quotes properly"
    (let [quotes (parse-quotes "se_quotes.json")]
      (is (not (empty? quotes)))
      (let [firstquote (get quotes 0)]
        (is (not (nil? (:author firstquote))))
        (is (not (nil? (:text firstquote))))))
  )
)
