(defproject {{ns-name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [ring/ring-json "0.3.1"]
                 [ring/ring-defaults "0.1.5"]
                 [scenic "0.2.3" :exclusions [org.clojure/tools.reader]]
                 [reloaded.repl "0.1.0"]
                 [com.stuartsierra/component "0.2.3"]
                 [metrics-clojure "2.5.1"]
                 [metrics-clojure-jvm "2.5.1"]
                 [metrics-clojure-graphite "2.5.1"]
                 [metrics-clojure-ring "2.5.1"]
                 [environ "1.0.0"]
                 [com.taoensso/timbre "3.4.0" :exclusions [org.clojure/tools.reader]]
                 [prismatic/schema "0.4.2"]
                 [robert/hooke "1.3.0"]
                 [dire "0.5.3"]
                 {{{project-deps}}}
                 [de.ubercode.clostache/clostache "1.4.0"]]

  :profiles {:uberjar {:aot :all             
                       :main {{ns-name}}.zygote}}

  :aliases {"omni" ["do"
                    ["clean"]
                    ["with-profile" "production" "deps" ":tree"]
                    ["ancient"]
                    ["kibit"]
                    ["bikeshed" "-m" "120"]
                    ["eastwood"]]
            "slamhound" ["run" "-m" "slam.hound"]})
