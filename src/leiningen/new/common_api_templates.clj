(ns leiningen.new.common-api-templates
  (:require [camel-snake-kebab.core :refer [->PascalCase]]
            [clojure.string :as string]
            [clostache.parser :refer [render]]
            [leiningen.new.db-template :refer :all]
;            [leiningen.new.mongo-template :refer :all]
            ))

(defn api-vals
  [ns-name]
  {:ns-name ns-name
   :path (string/replace ns-name "-" "_")
   :docker-name (string/replace ns-name "-" "")
   :dockerised-svr (str (->PascalCase ns-name) "DevSvr")})

(defn dev-profile
  [ns-name args]
  (let [lines (environment-variables args)
        template (string/join "\n             " lines)]
    (render template (api-vals ns-name))))

(defn project-deps
  [args]
  (->> (dependencies args)
       (string/join "\n                ")))

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
   :project-deps (project-deps options)
   :dev-profile (dev-profile ns-name options)})
