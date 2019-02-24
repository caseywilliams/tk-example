;; These are core methods used in the ruby bridge service; They are not used in other services.
(ns example.jruby-bridge-core
  (:require [clojure.tools.logging :as log]
            [schema.core :as schema]
            [puppetlabs.services.jruby-pool-manager.jruby-schemas :as jruby-schemas]
            [puppetlabs.services.jruby-pool-manager.jruby-core :as jruby-core]))

;; ruby-code-dir is the location of ruby code for this project, relative to
;; `src/ruby` (this is accessible because `src/ruby` is defined as a resource in
;; `project.clj`, placing it in the classpath and making it available on the
;; JRuby load path. It's also placed in the root of the uberjar, which is on the
;; classpath. See http://jruby.org/apidocs/org/jruby/runtime/load/LoadService.html.
(def ruby-code-dir "classpath:/example-lib")

;; An initialize-pool-instance method takes the JRuby instance as an argument
;; and modifies it if necessary, then returns it.
(defn initialize-pool-instance
  [jruby-instance]
  ;; The JRuby instance contains a ScriptingContainer:
  ;;   https://www.jruby.org/apidocs/org/jruby/embed/ScriptingContainer.html
  ;; jruby-utils wraps this container with its own InternalScriptingContainer, but the usual
  ;; ScriptingContainer methods are available.
  (let [scripting-container (:scripting-container jruby-instance)]
    ;; Any ScriptingContainer has a `runScriptlet` method on it that allows you to run ruby code.
    ;; Print out a message from ruby this way
    (.runScriptlet scripting-container "puts '==> Hello from a new jruby pool instance'")
    ;; Require our ruby bridge code in the container
    (.runScriptlet scripting-container "require 'example-lib/jruby_bridge'")
    (let [;; Fetch the ruby class that implements the JRubyBridge Java interface
          jruby-bridge-class (.runScriptlet scripting-container "Example::JRubyBridge")
          ;; Call new on the JRubyBridge class to allow us to interact with it
          jruby-bridge (.runScriptlet scripting-container "Example::JRubyBridge.new")]
      (log/info (str "Running ruby version " (.rubyVersion jruby-bridge)))
      (assoc jruby-instance :jruby-bridge jruby-bridge))))


(def default-jruby-config {;; Include 'ruby/example-lib' in ruby's LOAD_PATH so we can access its ruby code
                           :ruby-load-path [ruby-code-dir]
                           ;; Where JRuby gems will be installed
                           :gem-home       "/opt/puppetlabs/puppet/lib/ruby/gems/2.5.0"
                           ;; Where JRuby will look for gems
                           :gem-path       "/opt/puppetlabs/puppet/lib/ruby/gems/2.5.0"
                           ;; Override the default initialize-pool-instance code with our own method,
                           ;; which requires our ruby bridge code:
                           :lifecycle      {:initialize-pool-instance initialize-pool-instance}})


(schema/defn create-jruby-config :- jruby-schemas/JRubyConfig
  [] (jruby-core/initialize-config default-jruby-config))
