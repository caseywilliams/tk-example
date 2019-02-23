;; This file uses Ring to define a trapperkeeper service that runs a web app
;; (from example_web_core.clj).
;;
;; (Ring is a web applications library for Clojure like Rack for Ruby, or WSGI
;; for Python; See https://github.com/ring-clojure/ring.)
;;
;; Like the web app, this trapperkeeper service uses Compojure
;; (https://github.com/weavejester/compojure) to help with bolierplate Ring
;; functionality.
(ns example.example-web-service
  (:require [clojure.tools.logging :as log]
            [compojure.core :as compojure]
            [example.example-web-core :as web-core]
            [puppetlabs.trapperkeeper.core :as trapperkeeper]
            [puppetlabs.trapperkeeper.services :as tk-services]))

;; Define a trapperkeeper service called `hello-web-service`
(trapperkeeper/defservice hello-web-service
  ;; 1: Docstring
  "Hello web service"
  ;; ---
  ;; 2: Protocol
  ;; An (optional) protocol could come here, but this service doesn't use one.
  ;; ---
  ;; 3: Dependencies
  ;; Vector of [<symbolic-procotol-name> <import>...] of other trapperkeeper
  ;; services that this service depends on.
  ;;
  ;; This hello service depends on:
  ;; - some ConfigService, which provides a `get-in-config` method.
  ;; - some WebroutingService, which provides `add-ring-handler` and
  ;;   `get-route` methods. This will be from
  ;;   puppetlabs/trapperkeeper-webserver-jetty9 (see docs there).
  [[:ConfigService get-in-config]
   [:WebroutingService add-ring-handler get-route]]
  ;; 4: Lifecycle
  (init [this context]
    (log/info "Initializing hello webservice")
    ;; This service's `url-prefix` is supplied by the WebroutingService's
    ;; `get-route`, and will refer to the config.conf entry for this service,
    ;; like this:
    ;;
    ;;  web-router-service: {
    ;;     "example.example-web-service/hello-web-service": "/hello"
    ;;  }
    ;;
    (let [url-prefix (get-route this)]
      ;; The WebroutingService's add-ring-handler sets up some handler method
      ;; that should respond to requests for anything under some common url
      ;; segment (the prefix).
      (add-ring-handler
        this
        ;; Designate the app method from the web service as the handler for
        ;; any requests on the route for our url prefix ("/hello/...").
        (compojure/context url-prefix []
          ;; This app method requires a service context as the argument
          ;; Use trapperkeeper's get-service method to fetch the example
          ;; service from the trapperkeeper app by its protocol name:
          (web-core/app (tk-services/get-service this :HelloService))))
      ;; Also store the current URL prefix in the context
      (assoc context :url-prefix url-prefix)))

  (start [this context]
         ;; All this start method does is print a notice.
         ;; it returns the app context.
         (let [;; This `get-in-config` part, which comes from a ConfigService,
               ;; expects to find something like this in the app's config.conf:
               ;;
               ;;  webserver: {
               ;;      host: localhost
               ;;      port: 8080
               ;;  }
               host (get-in-config [:webserver :host])
               port (get-in-config [:webserver :port])
               ;; Like the init method, this call to get-route looks up the
               ;; "/hello/" url prefix configured in config.conf
               url-prefix (get-route this)]
              (log/infof "Hello web service started; visit http://%s:%s%s/world to check it out!"
                         host port url-prefix))
         context))

;; Note this service doesn't implement stop, there would be nothing to do there.
