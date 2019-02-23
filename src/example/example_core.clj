(ns example.example-core)

;; These are core methods for use in the example service; they are not used in other services.
(defn hello
  "Say hello to person"
  [person]
  (format "Hello, %s!" person))
