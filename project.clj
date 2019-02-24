(def ks-version "2.5.2")
(def tk-version "1.5.6")
(def tk-jetty9-version "2.3.1")

(defproject example "0.1.0-SNAPSHOT"
  :description "Example service"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :pedantic? :abort

  ;; Leinengen plugins:
  :plugins [;; lein-parent allows us to inherit package verisons from clj-parent
            [lein-parent "0.3.1"]]

  ;; clj-parent is like puppet-runtime - it controls versions of several dependencies
  :parent-project {:coords [puppetlabs/clj-parent "1.7.3"]
                   :inherit [:managed-dependencies]}

  ;;
  ;; Dependency packages:
  ;;
  :dependencies [;; JRuby helpers:
                 [puppetlabs/jruby-utils "2.0.0"]
                 [puppetlabs/jruby-deps "9.1.16.0-1"]

                 ;;
                 ;; Versions below are provided by clj-parent:
                 ;;

                 ;; Clojure itself
                 [org.clojure/clojure]
                 ;; Reads serialized data from Extensible Data Notation (EDN)
                 ;; https://learnxinyminutes.com/docs/edn/
                 [org.clojure/tools.reader]
                 ;; Logging
                 [org.clojure/tools.logging]
                 ;; Web server
                 [puppetlabs/trapperkeeper-webserver-jetty9]
                 ;; URL routing library
                 [compojure]
                 ;; Enhanced try and throw methods
                 [slingshot]
                 ;; Date and time library
                 [clj-time]
                 ;; Improved stack traces
                 [clj-stacktrace]
                 ;; General utilities
                 [puppetlabs/kitchensink]
                 ;; Trapperkeeper itself
                 [puppetlabs/trapperkeeper]]

  ;; Leiningen profiles allow you to load development dependencies in certain
  ;; contexts only.
  ;; See https://github.com/technomancy/leiningen/blob/master/doc/PROFILES.md
  ;; The `:dev` profile is for local development tooling (building and testing)
  :profiles {:dev {
                   ;; Makes leiningen look under ./dev for source code, in
                   ;; addition to ./src. This loads helpers for use in
                   ;; controlling trapperkeeper from the repl, like `go`; see
                   ;; the readme and ./dev/dev-resource.clj.
                   ;; See https://github.com/technomancy/leiningen/blob/master/doc/MIXED_PROJECTS.md#source-layout
                   :source-paths ["dev"]
                   :dependencies [[puppetlabs/trapperkeeper ~tk-version :classifier "test" :scope "test"]
                                  [puppetlabs/kitchensink ~ks-version :classifier "test" :scope "test"]
                                  [clj-http "3.0.0"]
                                  ;; Get nrepl from clj-parent:
                                  ;; N.B. nrepl is configured through a built-in trapperkeeper service. It's
                                  ;; loaded in bootstrap.cfg and configured in config.conf (these are in the
                                  ;; dev-resources folder)
                                  [org.clojure/tools.nrepl]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [ring-mock "0.1.5"]]}}

  ;; Makes `lein repl` start in the `user` namespace, which gets helpers from ./dev/user.clj
  :repl-options {:init-ns user}

  ;; Makes `lein run` run the main trapperkeeper function
  :main puppetlabs.trapperkeeper.main

  ;; Make Java classes accessible on the classpath for JRuby
  :java-source-paths ["src/java"]

  ;; Make Ruby classes accessible on the classpath for JRuby
  :resource-paths ["src/ruby"]

  ;; Makes `lein tk` run `lein run` to start trapperkeeper and pass it the
  ;; config files in dev-resources. It also wraps the `run` command with
  ;; `trampoline`, which allows leiningen to quit after the app starts so that
  ;; only one JVM process is running.
  :aliases {"tk" ["trampoline" "run" "--config" "dev-resources/config.conf"]}
  )
