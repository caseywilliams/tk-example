;; This file describes a "HelloService" trapperkepeer service.
(ns example.example-service
  (:require [clojure.tools.logging :as log]
            [example.example-core :as core]
            [puppetlabs.trapperkeeper.core :as trapperkeeper]))

;; In Clojure, a protocol is a named set of methods and their signatures,
;; like an interface, see https://clojure.org/reference/protocols.
;;
;; Most trapperkeeper services have a corresponding protocol like this; different
;; implementations of this protocol can be switched in and out by including
;; them in bootstrap.cfg, but they all participate in the same protocol so that
;; trapperkeeper can reliably expect to find a certain set of methods on them.
;;
;; If a trapperkeeper service doesn't use a protocol, it can have only one implementation.
;;
;; Any types participating in this HelloService protocol are guaranteed to
;; provide greeting functions...
(defprotocol HelloService
  ;; ...they should offer a function called `hello`, which takes
  ;; a `person` argument:
  ;; See https://clojuredocs.org/clojure.core/defprotocol.
  (hello [this person]))

;; trapperkeeper/defservice defines a new service. It accepts:
;; - an optional protocol, and
;; - a set of init/start/stop functions to be called in the trapperkeeper app.
;;
;; This service will only be loaded into the trapperkeeper app when it runs if
;; the following line is included in bootstrap.cfg:
;;     example.example-service/hello-service
;;
;; When loaded, this service will be stored in the app's context object
;; with a symbolic key matching its protocol name (if a service doesn't
;; have a protocol, it will be given a random, private name to use as a key).
(trapperkeeper/defservice hello-service
  ;; 1: Docstring
  "A service that says hello"
  ;; 2: Protocol
  HelloService
  ;; 3: Dependencies
  ;; This service has none.
  []
  ;; 4: Lifecycle
  ;; All trapperkeeper services can (but don't have to) implement init,
  ;; start, and stop, which will automatically called over the lifecycle
  ;; of the trapperkeeper app.
  ;; If implemented, these methods should each return the context.
  (init [this context]
    (log/info "Initializing hello service")
    context)
  (start [this context]
    (log/info "Starting hello service")
    context)
  (stop [this context]
    (log/info "Shutting down hello service")
    context)
  ;; Services can define extra functions that become accessible to other
  ;; services running within the trapperkeeper app that depend on this service.
  ;; This functionality should be used sparingly.
  ;; - This example service exposes a hello method:
  (hello [this person]
         (core/hello person)))
