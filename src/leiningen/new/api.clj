(ns leiningen.new.api
  (:require [leiningen.new.templates :refer [renderer]]
            [leiningen.new.mongo-template :refer :all]
            [leiningen.new.db-template :refer :all]))

(def render (renderer "life"))

(defn component-files
  [data]
  [["{{sanitized-api}}/src/{{sanitized-api}}/components/graphite/lifecycle.clj" (render "src/components/graphite/lifecycle.clj" data)]])

(defn controllers-files
  [data]
  [["{{sanitized-api}}/src/{{sanitized-api}}/controllers/api/core.clj" (render "src/controllers/api/core.clj" data)]
   ["{{sanitized-api}}/src/{{sanitized-api}}/controllers/healthcheck/lifecycle.clj" (render "src/controllers/healthcheck/lifecycle.clj" data)]
   ["{{sanitized-api}}/src/{{sanitized-api}}/controllers/healthcheck/core.clj" (render "src/controllers/healthcheck/core_api.clj" data)]])

(defn templates-files
  [data]
  [["{{sanitized-api}}/resources/templates/shared/default.mustache" (render "resources/templates/shared/default.mustache" data)]
   ["{{sanitized-api}}/resources/templates/shared/header.mustache" (render "resources/templates/shared/header.mustache" data)]
   ["{{sanitized-api}}/resources/templates/shared/footer.mustache" (render "resources/templates/shared/footer.mustache" data)]
   ["{{sanitized-api}}/resources/templates/healthcheck/healthcheck-list.mustache" (render "resources/templates/healthcheck/healthcheck-list.mustache" data)]])

(defn src-files
  [data] 
  [["{{sanitized-api}}/src/{{sanitized-api}}/zygote.clj" (render "src/zygote.clj" data)]
   ["{{sanitized-api}}/src/{{sanitized-api}}/logging_config.clj" (render "src/logging_config.clj" data)]
   ["{{sanitized-api}}/src/{{sanitized-api}}/responses.clj" (render "src/responses.clj" data)]])

(defn test-files
  [data]
  [["{{sanitized-api}}/test/{{sanitized-api}}/checkers/core.clj" (render "test/checkers/core.clj" data)]
   ["{{sanitized-api}}/test/{{sanitized-api}}/unit/controllers/healthcheck/core.clj" (render "test/unit/controllers/healthcheck/core.clj" data)]
   ["{{sanitized-api}}/test/{{sanitized-api}}/unit/components/graphite/lifecycle.clj" (render "test/unit/components/graphite/lifecycle.clj" data)]])

(defn dashboards-files
  [data]
  [["dashboards/dashboard-loader.js" (render "dashboards/dashboard-loader.js" data)]
   ["dashboards/app-stats.json" (render "dashboards/api-stats.json" data)]])

(defn resources-files
  [data]
  [["{{sanitized-api}}/resources/routes.txt" (render "resources/routes_api.txt")]
   ["{{sanitized-api}}/resources/public/css/styles.css" (render "resources/public/css/styles.css" data)]])

(defn dev-files
  [data]
  [["{{sanitized-api}}/dev/user.clj" (render "dev/user.clj" data)]])

(defn project-files
  [data]
  [["{{sanitized-api}}/project.clj" (render "project.clj" data)]
   ["{{sanitized-api}}/profiles.clj" (render "profiles.clj" data)]
   ["{{sanitized-api}}/Dockerfile" (render "Dockerfile" data)]
   ["{{sanitized-api}}/.dockerignore" (render "dockerignore" data)]
   ["{{sanitized-api}}/.gitignore" (render "gitignore" data)]
   ["{{sanitized-api}}/.midje.clj" (render "midje.clj" data)]
   ["{{sanitized-api}}/README.md" (render "README.md" data)]])

(defn api-files
  [data args]
  (concat (src-files data)
          (test-files data)
          (dashboards-files data)
          (resources-files data)
          (dev-files data)
          (project-files data)
          (controllers-files data)
          (templates-files data)
          (component-files data)
          (db-api-files args data)))
