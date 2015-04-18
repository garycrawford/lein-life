(ns leiningen.new.api
  (:require [leiningen.new.templates :refer [renderer]]))

(def render (renderer "rents"))

(defn component-files
  [data {:keys [db]}]
  (let [files [(when (= db :mongodb)
                 ["{{sanitized-api}}/src/{{sanitized-api}}/components/mongodb/core.clj" (render "src/components/mongodb/core.clj" data)])
               (when (= db :mongodb)
                 ["{{sanitized-api}}/src/{{sanitized-api}}/components/mongodb/lifecycle.clj" (render "src/components/mongodb/lifecycle.clj" data)])
               (if (= db :mongodb)
                 ["{{sanitized-api}}/src/{{sanitized-api}}/components/jetty/lifecycle.clj" (render "src/components/jetty/lifecycle_api_ext.clj" data)]
                 ["{{sanitized-api}}/src/{{sanitized-api}}/components/jetty/lifecycle.clj" (render "src/components/jetty/lifecycle_api_int.clj" data)])
               ["{{sanitized-api}}/src/{{sanitized-api}}/components/graphite/lifecycle.clj" (render "src/components/graphite/lifecycle.clj" data)]
               ["{{sanitized-api}}/src/{{sanitized-api}}/components/system.clj" (render "src/components/system.clj" data)]]]
    (remove nil? files)))

(defn controllers-files
  [data {:keys [db]}]
  (let [files
        [(when (= db :mongodb)
           ["{{sanitized-api}}/src/{{sanitized-api}}/controllers/people/lifecycle.clj" (render "src/controllers/people/lifecycle.clj" data)])
         (if (= db :mongodb)
           ["{{sanitized-api}}/src/{{sanitized-api}}/controllers/people/core.clj" (render "src/controllers/people/core_api_ext.clj" data)]
           ["{{sanitized-api}}/src/{{sanitized-api}}/controllers/people/core.clj" (render "src/controllers/people/core_api_int.clj" data)])
         ["{{sanitized-api}}/src/{{sanitized-api}}/controllers/api/core.clj" (render "src/controllers/api/core.clj" data)]
         ["{{sanitized-api}}/src/{{sanitized-api}}/controllers/healthcheck/lifecycle.clj" (render "src/controllers/healthcheck/lifecycle.clj" data)]
         ["{{sanitized-api}}/src/{{sanitized-api}}/controllers/healthcheck/core.clj" (render "src/controllers/healthcheck/core_api.clj" data)]]]
    (remove nil? files)))

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
  [data {:keys [db]}]
  (let [files
        [(when (= db :mongodb)
           ["{{sanitized-api}}/test/{{sanitized-api}}/unit/components/mongodb/core.clj" (render "test/unit/components/mongodb/core.clj" data)])
         (when (= db :mongodb)
           ["{{sanitized-api}}/test/{{sanitized-api}}/unit/controllers/people/core.clj" (render "test/unit/controllers/people/core.clj" data)])
         (when (= db :mongodb)
           ["{{sanitized-api}}/test/{{sanitized-api}}/integration/controllers/people/core.clj" (render "test/integration/controllers/people/core.clj" data)])
         ["{{sanitized-api}}/test/{{sanitized-api}}/unit/controllers/healthcheck/core.clj" (render "test/unit/controllers/healthcheck/core.clj" data)]
         ["{{sanitized-api}}/test/{{sanitized-api}}/unit/components/graphite/lifecycle.clj" (render "test/unit/components/graphite/lifecycle.clj" data)]]]
    (remove nil? files)))

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
          (test-files data args)
          (dashboards-files data)
          (resources-files data)
          (dev-files data)
          (project-files data)
          (controllers-files data args)
          (templates-files data)
          (component-files data args)))
