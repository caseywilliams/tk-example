;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Utilities for use in a repl
;; These are accessible by default when you run `lein repl`.
;; Use them to interact with a running instance of your tk app.
(ns user
  (:require [clojure.pprint :as pprint]
            [clojure.tools.namespace.repl :refer [refresh]]
            [puppetlabs.trapperkeeper.app :as tka]
            [puppetlabs.trapperkeeper.bootstrap :as bootstrap]
            [puppetlabs.trapperkeeper.config :as config]
            [puppetlabs.trapperkeeper.core :as tk]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Control the tk lifecycle

(def system nil)

;; Create a development tk app, reading configuration from bootstrap.cfg and
;; config.conf in ./dev-resources. Don't start any services yet.
(defn init []
  (alter-var-root #'system
                  (fn [_] (tk/build-app
                            (bootstrap/parse-bootstrap-config! "./dev-resources/bootstrap.cfg")
                            (config/load-config "./dev-resources/config.conf"))))
  (alter-var-root #'system tka/init)
  (tka/check-for-errors! system))

;; Start up the init'd tk app
(defn start []
  (alter-var-root #'system
                  (fn [s] (if s (tka/start s))))
  (tka/check-for-errors! system))

;; Stop the tk app
(defn stop []
  (alter-var-root #'system
                  (fn [s] (when s (tka/stop s)))))

;; Init and start at once
(defn go []
  (init)
  (start))

;; Stop, refresh everything, then init and start again
(defn reset []
  (stop)
  (refresh :after 'user/go))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Utilities for interacting with a running app
;; These only work when tk is running.

(defn context
  "Get the current TK application context.  Accepts an optional array
  argument, which is treated as a sequence of keys to retrieve a nested
  subset of the map (a la `get-in`)."
  ([]
   (context []))
  ([keys]
   (get-in @(tka/app-context system) keys)))

(defn print-context
  "Pretty-print the current TK application context.  Accepts an optional
  array of keys (a la `get-in`) to print a nested subset of the context."
  ([]
   (print-context []))
  ([keys]
   (pprint/pprint (context keys))))
