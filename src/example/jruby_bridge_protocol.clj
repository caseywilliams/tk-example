(ns example.jruby-bridge-protocol)

(defprotocol JRubyBridgeService
  "Describes the JRubyBridgeService"
  (flush-jruby-pool!
    [this]
    "Flush all the current JRuby instances and repopulate the pool.")
  (free-instance-count
    [this]
    "The number of free JRubyBridge instances left in the pool.")
  (get-pool-context
    [this]
    "Get the pool context out of the service context.")
  (register-event-handler
    [this callback]
    "Register a callback function to receive notifications when JRuby service events occur.
    The callback fn should accept a single arg, which will conform to the JRubyEvent schema."))

