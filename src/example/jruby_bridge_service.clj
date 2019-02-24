(ns example.jruby-bridge-service
  (:require [clojure.tools.logging :as log]
            [example.jruby-bridge-protocol :as jruby]
            [example.jruby-bridge-core :as core]
            [puppetlabs.services.jruby-pool-manager.jruby-core :as jruby-core]
            [puppetlabs.trapperkeeper.core :as trapperkeeper]
            [puppetlabs.trapperkeeper.services :as tk-services]))

;; puppetlabs/jruby-core provides a `with-jruby-instance` macro that borrows a
;; JRuby instance from the pool and returns it when done, but that instance
;; won't have our JRubyBridge class loaded. This macro wraps
;; `with-jruby-instance` to include the JRubyBridge.
(defmacro with-jruby-bridge
  "A convenience macro that wraps jruby/with-jruby-instance. jruby-bridge is bound
  to the jruby-bridge object found in the borrowed jruby-instance"
  [jruby-bridge jruby-service reason & body]
  `(let [pool-context# (jruby/get-pool-context ~jruby-service)]
     (jruby-core/with-jruby-instance
       jruby-instance#
       pool-context#
       ~reason
       (let [~jruby-bridge (:jruby-bridge jruby-instance#)]
         ~@body))))

;; Define the jruby bridge trapperkeeper service
(trapperkeeper/defservice jruby-bridge-pooled-service
                          "Pooled JRubyBridge service"
                          jruby/JRubyBridgeService
                          [[:ConfigService get-config]
                           [:ShutdownService shutdown-on-error]
                           [:PoolManagerService create-pool]]

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Trapperkeeper lifecycle methods

  (init
    [this context]
    (log/info "Initializing JRuby service")
    (let [;; Get a default config object for jruby
          jruby-config (core/create-jruby-config)
          ;; Create the jruby pool for this service using the jruby config
          pool-context (create-pool jruby-config)]
      ;; Log the jruby version
      (log/info "JRuby version info: " jruby-core/jruby-version-info)
      ;; Store the pool we created in the context for this service
      (assoc context :pool-context pool-context)))

  (start
    [this context]
    (log/info "Starting JRuby service")
    ;; Fetch this service's context from trapperkeeper (we want access to the jruby pool)
    (let [{:keys [pool-context]} (tk-services/service-context this)]
      ;; Borrow a jruby instance and log it to see that it's there
      (jruby-core/with-jruby-instance jruby-instance pool-context :testing-with-jruby-instance
                                      (log/info (str "JRuby instance: " jruby-instance))))
    context)

  (stop
    [this context]
    (log/info "Stopping JRuby service")
    (let [{:keys [pool-context]} (tk-services/service-context this)]
      ;; Only flush the pool if a pool-context is available:
      (when pool-context
        (jruby-core/flush-pool-for-shutdown! pool-context)))
    context)

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Other methods exposed to other services

  ;; Free instance count
  (free-instance-count
    [this]
    (let [pool-context (:pool-context (tk-services/service-context this))
          pool         (jruby-core/get-pool pool-context)]
      (jruby-core/free-instance-count pool)))

  ;; Flush the JRuby pool
  (flush-jruby-pool!
    [this]
    (let [service-context (tk-services/service-context this)
          {:keys [pool-context]} service-context]
      (jruby-core/flush-pool! pool-context)))

  ;; Getter for the pool context stored in this service's context during init
  (get-pool-context
    [this]
    (:pool-context (tk-services/service-context this)))

  ;; Register an event handler
  (register-event-handler
    [this callback-fn]
    (let [pool-context (:pool-context (tk-services/service-context this))]
      (jruby-core/register-event-handler pool-context callback-fn))))

