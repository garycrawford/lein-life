(ns leiningen.new.site
  (:require [leiningen.new.templates :refer [renderer]]
            [leiningen.new.mongo-template :refer :all]
            [leiningen.new.api-template :refer :all]
            [leiningen.new.db-template :refer :all]))

(def render (renderer "life"))

(defn component-files
  [data]
  [["{{sanitized-site}}/src/{{sanitized-site}}/components/graphite/lifecycle.clj" (render "src/components/graphite/lifecycle.clj" data)]])

(defn controllers-files
  [data]
  [["{{sanitized-site}}/src/{{sanitized-site}}/controllers/healthcheck/lifecycle.clj" (render "src/controllers/healthcheck/lifecycle.clj" data)]
   ["{{sanitized-site}}/src/{{sanitized-site}}/controllers/healthcheck/core.clj" (render "src/controllers/healthcheck/core_site.clj" data)]])

(defn models-files
  [data]
  [["{{sanitized-site}}/src/{{sanitized-site}}/models/healthcheck.clj" (render "src/models/healthcheck.clj" data)]])

(defn views-files
  [data]
  [["{{sanitized-site}}/src/{{sanitized-site}}/views/home.clj" (render "src/views/home.clj" data)]
   ["{{sanitized-site}}/src/{{sanitized-site}}/views/healthcheck.clj" (render "src/views/healthcheck.clj" data)]
   ["{{sanitized-site}}/src/{{sanitized-site}}/views/shared.clj" (render "src/views/shared.clj" data)]])

(defn templates-files
  [data]
  [["{{sanitized-site}}/resources/templates/home/introduction.mustache" (render "resources/templates/home/introduction.mustache" data)]
   ["{{sanitized-site}}/resources/templates/home/add-person.mustache" (render "resources/templates/home/add-person.mustache" data)]
   ["{{sanitized-site}}/resources/templates/home/delete-person.mustache" (render "resources/templates/home/delete-person.mustache" data)]
   ["{{sanitized-site}}/resources/templates/home/update-person.mustache" (render "resources/templates/home/update-person.mustache" data)]
   ["{{sanitized-site}}/resources/templates/home/person-list.mustache" (render "resources/templates/home/person-list.mustache" data)]
   ["{{sanitized-site}}/resources/templates/home/not-found.mustache" (render "resources/templates/home/not-found.mustache" data)]
   ["{{sanitized-site}}/resources/templates/healthcheck/healthcheck-list.mustache" (render "resources/templates/healthcheck/healthcheck-list.mustache" data)]
   ["{{sanitized-site}}/resources/templates/shared/default.mustache" (render "resources/templates/shared/default.mustache" data)]
   ["{{sanitized-site}}/resources/templates/shared/header.mustache" (render "resources/templates/shared/header.mustache" data)]
   ["{{sanitized-site}}/resources/templates/shared/footer.mustache" (render "resources/templates/shared/footer.mustache" data)]])

(defn src-files
  [data] 
  [["{{sanitized-site}}/src/{{sanitized-site}}/zygote.clj" (render "src/zygote.clj" data)]
   ["{{sanitized-site}}/src/{{sanitized-site}}/logging_config.clj" (render "src/logging_config.clj" data)]
   ["{{sanitized-site}}/src/{{sanitized-site}}/responses.clj" (render "src/responses.clj" data)]])

(defn test-files
  [data]
  [["{{sanitized-site}}/test/{{sanitized-site}}/checkers/core.clj" (render "test/checkers/core.clj" data)]
   ["{{sanitized-site}}/test/{{sanitized-site}}/unit/components/graphite/lifecycle.clj" (render "test/unit/components/graphite/lifecycle.clj" data)]
   ["{{sanitized-site}}/test/{{sanitized-site}}/unit/controllers/healthcheck/core.clj" (render "test/unit/controllers/healthcheck/core.clj" data)]])

(defn dashboards-files
  [data]
  [["dashboards/dashboard-loader.js" (render "dashboards/dashboard-loader.js" data)]
   ["dashboards/app-stats.json" (render "dashboards/site-stats.json" data)]])

(defn resources-files
  [data]
  [["{{sanitized-site}}/resources/public/css/styles.css" (render "resources/public/css/styles.css" data)]
   ["{{sanitized-site}}/resources/routes.txt" (render "resources/routes_site.txt")]])

(defn dev-files
  [data]
  [["{{sanitized-site}}/dev/user.clj" (render "dev/user.clj" data)]])

(defn project-files
  [data]
  [["{{sanitized-site}}/project.clj" (render "project.clj" data)]
   ["{{sanitized-site}}/profiles.clj" (render "profiles.clj" data)]
   ["{{sanitized-site}}/Dockerfile" (render "Dockerfile" data)]
   ["{{sanitized-site}}/.dockerignore" (render "dockerignore" data)]
   ["{{sanitized-site}}/.gitignore" (render "gitignore" data)]
   ["{{sanitized-site}}/.midje.clj" (render "midje.clj" data)]
   ["{{sanitized-site}}/README.md" (render "README.md" data)]])

(defn site-files
  [data args]
  (concat (component-files data)
          (controllers-files data)
          (models-files data)
          (views-files data)
          (templates-files data)
          (src-files data)
          (test-files data)
          (dashboards-files data)
          (resources-files data)
          (dev-files data)
          (project-files data)
          (db-site-files args data)))
