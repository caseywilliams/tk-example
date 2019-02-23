;; These are core methods for use in the example web service; they are not used
;; in other services.
(ns example.example-web-core
  (:require [example.example-service :as example-service]
            [clojure.tools.logging :as log]
            ;; Compojure is a routing tool for Ring web apps. It parses URL
            ;; routes and responds to them, see
            ;; https://github.com/weavejester/compojure:
            [compojure.core :as compojure]
            ;; These are reusable routes for common stuff like 404 pages:
            [compojure.route :as route]))

;; A web app that exposes an API. The API is an interface to the example hello
;; service defined in example_service.clj.  The web app's "main" is this `app`
;; method
(defn app
  [service-context] ;; ...it takes the example hello service as an argument.
  ;; This routes function defines what will happen when users access different
  ;; endpoints of the API:
  (compojure/routes
    (compojure/GET "/:person" [person]
      ;; When a user 'GET's the base url plus some other string (referred to as
      ;; `person`), respond as follows:
      (fn [req]
        ;; Log something
        (log/info "Handling request for person:" person)
        ;; ...and call the hello service to send this HTTP response:
        {:status  200
         :headers {"Content-Type" "text/plain"}
         ;; Important part: building the response body:
         ;; - Look at the definition of the example service to call its `hello` method,
         ;;   which it exposes alonside its init, start, and stop methods.
         ;; - `service-context` is the actual instance of the service
         ;;   the protocol, which was passed in as an argument to this function.
         ;;   We're passing this to the hello method as its context.
         ;; - We're passing the `person` we read from the end of the URL as the
         ;;   second argument to `hello`.
         :body    (example-service/hello service-context person)}))
    ;; This helper generates a 404 for any invalid route and responds "Not Found"
    (route/not-found "Not Found")))
