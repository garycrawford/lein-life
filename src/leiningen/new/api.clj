(ns leiningen.new.api
  (:require [leiningen.new.templates :refer [renderer]]))

(def render (renderer "rents"))

(defn component-files
  [data {:keys [db]}]
  (let [files [(when (= db :mongodb)
                 ["src/{{sanitized}}/components/mongodb/core.clj" (render "src/components/mongodb/core.clj" data)])
               (when (= db :mongodb)
                 ["src/{{sanitized}}/components/mongodb/lifecycle.clj" (render "src/components/mongodb/lifecycle.clj" data)])
               (if (= db :mongodb)
                 ["src/{{sanitized}}/components/jetty/lifecycle.clj" (render "src/components/jetty/lifecycle_api_ext.clj" data)]
                 ["src/{{sanitized}}/components/jetty/lifecycle.clj" (render "src/components/jetty/lifecycle_api_int.clj" data)])
               ["src/{{sanitized}}/components/graphite/lifecycle.clj" (render "src/components/graphite/lifecycle.clj" data)]
               ["src/{{sanitized}}/components/system.clj" (render "src/components/system.clj" data)]]]
    (remove nil? files)))

(defn controllers-files
  [data {:keys [db]}]
  (let [files
        [(when (= db :mongodb)
           ["src/{{sanitized}}/controllers/people/lifecycle.clj" (render "src/controllers/people/lifecycle.clj" data)])
         (if (= db :mongodb)
           ["src/{{sanitized}}/controllers/people/core.clj" (render "src/controllers/people/core_api_ext.clj" data)]
           ["src/{{sanitized}}/controllers/people/core.clj" (render "src/controllers/people/core_api_int.clj" data)])
         ["src/{{sanitized}}/controllers/api/core.clj" (render "src/controllers/api/core.clj" data)]
         ["src/{{sanitized}}/controllers/healthcheck/lifecycle.clj" (render "src/controllers/healthcheck/lifecycle.clj" data)]
         ["src/{{sanitized}}/controllers/healthcheck/core.clj" (render "src/controllers/healthcheck/core_api.clj" data)]]]
    (remove nil? files)))

(defn templates-files
  [data]
  [["resources/templates/shared/default.mustache" (render "resources/templates/shared/default.mustache" data)]
   ["resources/templates/shared/header.mustache" (render "resources/templates/shared/header.mustache" data)]
   ["resources/templates/shared/footer.mustache" (render "resources/templates/shared/footer.mustache" data)]
   ["resources/templates/healthcheck/healthcheck-list.mustache" (render "resources/templates/healthcheck/healthcheck-list.mustache" data)]])

(defn src-files
  [data] 
  [["src/{{sanitized}}/zygote.clj" (render "src/zygote.clj" data)]
   ["src/{{sanitized}}/logging_config.clj" (render "src/logging_config.clj" data)]
   ["src/{{sanitized}}/responses.clj" (render "src/responses.clj" data)]])

(defn public-files
  [data]
  [["resources/public/css/styles.css" (render "resources/public/css/styles.css" data)]])

(defn test-files
  [data {:keys [db]}]
  (let [files
        [(when (= db :mongodb)
           ["test/{{sanitized}}/unit/components/mongodb/core.clj" (render "test/unit/components/mongodb/core.clj" data)])
         (when (= db :mongodb)
           ["test/{{sanitized}}/unit/controllers/people/core.clj" (render "test/unit/controllers/people/core.clj" data)])
         (when (= db :mongodb)
           ["test/{{sanitized}}/integration/controllers/people/core.clj" (render "test/integration/controllers/people/core.clj" data)])
         ["test/{{sanitized}}/unit/controllers/healthcheck/core.clj" (render "test/unit/controllers/healthcheck/core.clj" data)]
         ["test/{{sanitized}}/unit/components/graphite/lifecycle.clj" (render "test/unit/components/graphite/lifecycle.clj" data)]]]
    (remove nil? files)))

(defn dashboards-files
  [data]
  [["dashboards/dashboard-loader.js" (render "dashboards/dashboard-loader.js" data)]
   ["dashboards/app-stats.json" (render "dashboards/app-stats.json" data)]])

(defn resources-files
  [data]
  [["resources/routes.txt" (render "resources/routes_api.txt")]])

(defn dev-files
  [data]
  [["dev/user.clj" (render "dev/user.clj" data)]])

(defn project-files
  [data]
   [["project.clj" (render "project.clj" data)]
    ["profiles.clj" (render "profiles.clj" data)]
    ["Dockerfile" (render "Dockerfile" data)]
    ["docker-compose.yml" (render "docker-compose.yml" data)]
    [".dockerignore" (render "dockerignore" data)]
    [".gitignore" (render "gitignore" data)]
    [".midje.clj" (render "midje.clj" data)]
    ["README.md" (render "README.md" data)]])

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
          (public-files data)
          (component-files data args)))
