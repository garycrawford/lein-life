(defproject rents/lein-template "0.0.1"
  :description "Lein template for generating dockerised Clojure sites and apis"
  :url "https://github.com/garycrawford/lein-rents"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true
  :dependencies [[camel-snake-kebab "0.3.1" :exclusions [org.clojure/clojure]]
                 [org.clojure/tools.cli "0.3.1"]
                 [midje "1.6.3"]
                 [org.clojure/test.check "0.7.0"]
                 [com.gfredericks/test.chuck "0.1.16"]
                 [kerodon "0.5.0"]]
  :scm {:name "git"
        :url "https://github.com/garycrawford/lein-rents"}
  :plugins [[lein-shell "0.4.0"]]
  :aliases {"." ["do"
                  ["shell" "scripts/update-examples.sh"]
                  ["shell" "scripts/run-tests.sh" "example-site"]
                  ["shell" "scripts/run-tests.sh" "example-api"]]})
