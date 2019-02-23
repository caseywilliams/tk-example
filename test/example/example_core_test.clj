(ns example.example-core-test
  (:require [clojure.test :refer :all]
            [example.example-core :refer :all]))

(deftest hello-test
  (testing "says hello to caller"
    (is (= "Hello, foo!" (hello "foo")))))
