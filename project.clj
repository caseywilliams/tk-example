(def ks-version "2.5.2")
(def tk-version "1.5.6")
(def tk-jetty9-version "2.3.1")

(defproject example "0.1.0-SNAPSHOT"
  :description "Example service"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :pedantic? :abort

  :parent-project {:coords [puppetlabs/clj-parent "1.7.3"]
                   :inherit [:managed-dependencies]}

  :plugins [[lein-parent "0.3.1"]
            [puppetlabs/i18n "0.8.0"]]

  :dependencies [[org.clojure/clojure]

                 ;; explicit versions of deps that would cause transitive dep conflicts
                 [org.clojure/tools.reader "1.2.1"]
                 [slingshot]
                 [clj-time]
                 ;; end explicit versions of deps that would cause transitive dep conflicts
                 [compojure "1.5.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [puppetlabs/jruby-utils "2.0.0"]
                 [puppetlabs/jruby-deps "9.1.16.0-1"]
                 [puppetlabs/trapperkeeper]
                 [puppetlabs/kitchensink]
                 [puppetlabs/trapperkeeper-webserver-jetty9 ~tk-jetty9-version]]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[puppetlabs/trapperkeeper ~tk-version :classifier "test" :scope "test"]
                                  [puppetlabs/kitchensink ~ks-version :classifier "test" :scope "test"]
                                  [clj-http "3.0.0"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [ring-mock "0.1.5"]]}}

  :repl-options {:init-ns user}

  :aliases {"tk" ["trampoline" "run" "--config" "dev-resources/config.conf"]}

  :main puppetlabs.trapperkeeper.main

  )
