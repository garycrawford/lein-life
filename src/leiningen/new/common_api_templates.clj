(ns leiningen.new.common-api-templates
  (:require [camel-snake-kebab.core :refer [->PascalCase]]
            [clojure.tools.cli :refer  [parse-opts]]
            [leiningen.new.api :refer [api-files]]
            [leiningen.new.site :refer [site-files]]
            [clojure.string :as string]
            [clostache.parser :refer [render]]))

(defn construct-template
  [lines]
  (->> lines
       (partition 2)
       (map (fn [[line check-fn]] (when (check-fn) line)))
       (filter (complement nil?))
       (string/join \newline)))

(def always (constantly true))
(def mongodb? (partial = :mongodb))

(defn api-vals
  [ns-name]
  {:ns-name ns-name
   :path (string/replace ns-name "-" "_")
   :docker-name (string/replace ns-name "-" "")
   :dockerised-svr (str (->PascalCase ns-name) "DevSvr")})

(defn dev-profile
  [ns-name {:keys [db]}]
  (let [template (-> ["{:dev {:source-paths [\"dev\"]"                                                                            always
                      "       :plugins [[lein-ancient \"0.6.3\"]"                                                                 always
                      "                 [jonase/eastwood \"0.2.1\"]"                                                              always
                      "                 [lein-bikeshed \"0.2.0\"]"                                                                always
                      "                 [lein-kibit \"0.0.8\"]"                                                                   always
                      "                 [lein-environ \"1.0.0\"]"                                                                 always
                      "                 [lein-midje \"3.1.3\"]]"                                                                  always
                      "       :dependencies [[org.clojure/tools.namespace \"0.2.10\"]"                                            always
                      "                      [slamhound \"1.5.5\"]"                                                               always
                      "                      [com.cemerick/pomegranate \"0.3.0\" :exclusions [org.codehaus.plexus/plexus-utils]]" always
                      "                      [prone \"0.8.1\"]"                                                                   always
                      "                      [midje \"1.6.3\"]"                                                                   always
                      "                      [org.clojure/test.check \"0.7.0\"]"                                                  always
                      "                      [com.gfredericks/test.chuck \"0.1.16\"]"                                             always
                      "                      [kerodon \"0.6.0\"]]"                                                                always
                      "       :env {:metrics-host \"192.168.59.103\""                                                             always
                      "             :metrics-port 2003"                                                                           always
                      "             :mongodb-uri  \"mongodb://192.168.59.103/{{path}}\""                                          #(mongodb? db)
                      "             :app-name     \"{{ns-name}}\""                                                                always
                      "             :hostname     \"{{dockerised-svr}}\"}"                                                        always
                      "       :ring {:stacktrace-middleware prone.middleware/wrap-exceptions}}}"                                  always]
                     construct-template)]
    (render template (api-vals ns-name))))

(defn project-deps
  [{:keys [db]}]
  (-> [" :dependencies [[org.clojure/clojure \"1.6.0\"]"                                        always
       "                [ring/ring-jetty-adapter \"1.3.2\"]"                                    always
       "                [ring/ring-json \"0.3.1\"]"                                             always
       "                [ring/ring-defaults \"0.1.4\"]"                                         always
       "                [scenic \"0.2.3\" :exclusions [org.clojure/tools.reader]]"              always
       "                [reloaded.repl \"0.1.0\"]"                                              always
       "                [com.stuartsierra/component \"0.2.3\"]"                                 always
       "                [metrics-clojure \"2.5.1\"]"                                            always
       "                [metrics-clojure-jvm \"2.5.1\"]"                                        always
       "                [metrics-clojure-graphite \"2.5.1\"]"                                   always 
       "                [metrics-clojure-ring \"2.5.1\"]"                                       always
       "                [environ \"1.0.0\"]"                                                    always
       "                [com.taoensso/timbre \"3.4.0\" :exclusions [org.clojure/tools.reader]]" always
       "                [prismatic/schema \"0.4.0\"]"                                           always
       "                [robert/hooke \"1.3.0\"]"                                               always
       "                [com.novemberain/monger \"2.1.0\"]"                                     #(mongodb? db)
       "                [jstrutz/hashids \"1.0.1\"]"                                            #(mongodb? db)
       "                [dire \"0.5.3\"]"                                                       always
       "                [de.ubercode.clostache/clostache \"1.4.0\"]]"                           always]
      construct-template))

(defn system-ns-str
  [ns-name {:keys [db]}]
  (let [template (-> ["(ns %1$s.components.system"                                                     always
                      "  (:require [com.stuartsierra.component :as component]"                         always
                      "            [metrics.core :refer [new-registry]]"                               always
                      "            [metrics.jvm.core :as jvm]"                                         always
                      "            [%1$s.components.graphite.lifecycle :refer [new-metrics-reporter]]" always
                      "            [%1$s.components.mongodb.lifecycle :refer [new-mongodb]]"           #(mongodb? db)
                      "            [%1$s.components.jetty.lifecycle :refer [new-web-server]]"          always
                      "            [%1$s.controllers.people.lifecycle :refer [new-people-controller]]" #(mongodb? db)
                      "            [%1$s.logging-config]))"                                            always]
                     construct-template)]
    (format template ns-name)))

(defn system-comp-list-str
  [{:keys [db]}]
  (-> ["(def components [:web-server"         always
       "                 :mongodb"            #(mongodb? db)
       "                 :metrics-registry"   always
       "                 :people"             #(mongodb? db)
       "                 :metrics-reporter])" always]
      construct-template))

(defn system-dep-graph
  [ns-name {:keys [db]}]
  (let [template (-> ["(defn new-%1s-system"                                                                   always
                      "  \"Constructs the component system for the application.\""                             always
                      "  []"                                                                                   always
                      "  (let [metrics-registry (new-registry)]"                                               always
                      "    (jvm/instrument-jvm metrics-registry)"                                              always
                      "    (map->Quotations-Web-System"                                                        always
                      "     {:web-server       (component/using (new-web-server) [:metrics-registry :people])" #(mongodb? db)
                      "     {:web-server       (component/using (new-web-server) [:metrics-registry])"         (complement #(mongodb? db))
                      "      :mongodb          (new-mongodb)"                                                  #(mongodb? db)
                      "      :metrics-reporter (component/using (new-metrics-reporter) [:metrics-registry])"   always
                      "      :people           (component/using (new-people-controller) [:mongodb])"           #(mongodb? db)
                      "      :metrics-registry  metrics-registry})))"                                          always]
                     construct-template)]
    (format template ns-name)))

(defn healthcheck-list-template
  []
  (->> ["<ul>"
        "{{#healthchecks}}"
        "  <li>{{name}}: {{status}}</li>"
        "{{/healthchecks}}"
        "</ul>"]
       (string/join \newline)))

(defn page-template
  []
  (->> ["{{>header}}"
        "  <div class=\"default\">"
        "    {{{content}}}"
        "  </div>"
        "{{>footer}}"]
       (string/join \newline)))

(defn api-var-map
  [ns-name options]
  {:healthcheck-list-template (healthcheck-list-template)
   :page-template (page-template)
   :system-ns (system-ns-str ns-name options)
   :system-comp-list (system-comp-list-str options)
   :system-dep-graph (system-dep-graph ns-name options)
   :project-deps (project-deps options)
   :dev-profile (dev-profile ns-name options)})
