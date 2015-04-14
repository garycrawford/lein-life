(ns leiningen.new.site
  (:require [leiningen.new.templates :refer [renderer]]))

(def render (renderer "rents"))

(defn component-files
  [data {:keys [db]}]
  (let [files [(when (= db :mongodb)
                 ["src/{{sanitized}}/components/mongodb/core.clj" (render "src/components/mongodb/core.clj" data)])
               (when (= db :mongodb)
                 ["src/{{sanitized}}/components/mongodb/lifecycle.clj" (render "src/components/mongodb/lifecycle.clj" data)])
               (if (= db :mongodb)
                 ["src/{{sanitized}}/components/jetty/lifecycle.clj" (render "src/components/jetty/lifecycle_site_ext.clj" data)]
                 ["src/{{sanitized}}/components/jetty/lifecycle.clj" (render "src/components/jetty/lifecycle_site_int.clj" data)])
               ["src/{{sanitized}}/components/system.clj" (render "src/components/system.clj" data)]
               ["src/{{sanitized}}/components/graphite/lifecycle.clj" (render "src/components/graphite/lifecycle.clj" data)]]]
    (remove nil? files)))

(defn controllers-files
  [data {:keys [db]}]
  (let [files
        [(when (= db :mongodb)
           ["src/{{sanitized}}/controllers/home/lifecycle.clj" (render "src/controllers/home/lifecycle.clj" data)])
         (if (= db :mongodb)
           ["src/{{sanitized}}/controllers/home/core.clj" (render "src/controllers/home/core_site_ext.clj" data)]
           ["src/{{sanitized}}/controllers/home/core.clj" (render "src/controllers/home/core_site_int.clj" data)])
         ["src/{{sanitized}}/controllers/healthcheck/lifecycle.clj" (render "src/controllers/healthcheck/lifecycle.clj" data)]
         ["src/{{sanitized}}/controllers/healthcheck/core.clj" (render "src/controllers/healthcheck/core_site.clj" data)]]]
    (remove nil? files)))

(defn models-files
  [data]
  [["src/{{sanitized}}/models/home.clj" (render "src/models/home.clj" data)]
   ["src/{{sanitized}}/models/healthcheck.clj" (render "src/models/healthcheck.clj" data)]])

(defn views-files
  [data]
  [["src/{{sanitized}}/views/home.clj" (render "src/views/home.clj" data)]
   ["src/{{sanitized}}/views/healthcheck.clj" (render "src/views/healthcheck.clj" data)]
   ["src/{{sanitized}}/views/shared.clj" (render "src/views/shared.clj" data)]])

(defn templates-files
  [data]
  [["resources/templates/home/introduction.mustache" (render "resources/templates/home/introduction.mustache" data)]
   ["resources/templates/home/welcome.mustache" (render "resources/templates/home/welcome.mustache" data)]
   ["resources/templates/healthcheck/healthcheck-list.mustache" (render "resources/templates/healthcheck/healthcheck-list.mustache" data)]
   ["resources/templates/shared/default.mustache" (render "resources/templates/shared/default.mustache" data)]
   ["resources/templates/shared/header.mustache" (render "resources/templates/shared/header.mustache" data)]
   ["resources/templates/shared/footer.mustache" (render "resources/templates/shared/footer.mustache" data)]])

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
        [(if (= db :mongodb)
           ["test/{{sanitized}}/unit/controllers/home/core.clj" (render "test/unit/controllers/home/core_site_ext.clj" data)]
           ["test/{{sanitized}}/unit/controllers/home/core.clj" (render "test/unit/controllers/home/core_site_int.clj" data)])
         ["test/{{sanitized}}/unit/controllers/healthcheck/core.clj" (render "test/unit/controllers/healthcheck/core.clj" data)]
         ["test/{{sanitized}}/unit/components/graphite/lifecycle.clj" (render "test/unit/components/graphite/lifecycle.clj" data)]]]
    (remove nil? files)))

(defn test-files
  [data {:keys [db]}]
  (let [files
        [(when (= db :mongodb)
           ["test/{{sanitized}}/unit/components/mongodb/core.clj" (render "test/unit/components/mongodb/core.clj" data)])
         ["test/{{sanitized}}/unit/controllers/healthcheck/core.clj" (render "test/unit/controllers/healthcheck/core.clj" data)]
         ["test/{{sanitized}}/unit/components/graphite/lifecycle.clj" (render "test/unit/components/graphite/lifecycle.clj" data)]]]
    (remove nil? files)))



(defn test-files
  [data {:keys [db]}]
  (let [files
        [(when (= db :mongodb)
           ["test/{{sanitized}}/unit/components/mongodb/core.clj" (render "test/unit/components/mongodb/core.clj" data)])
         (if (= db :mongodb)
           ["test/{{sanitized}}/integration/controllers/home/core.clj" (render "test/integration/controllers/home/core_site_ext.clj" data)]
           ["test/{{sanitized}}/integration/controllers/home/core.clj" (render "test/integration/controllers/home/core_site_int.clj" data)])
         (if (= db :mongodb)
           ["test/{{sanitized}}/unit/controllers/home/core.clj" (render "test/unit/controllers/home/core_site_ext.clj" data)]
           ["test/{{sanitized}}/unit/controllers/home/core.clj" (render "test/unit/controllers/home/core_site_int.clj" data)])
         ["test/{{sanitized}}/unit/components/graphite/lifecycle.clj" (render "test/unit/components/graphite/lifecycle.clj" data)]
         ["test/{{sanitized}}/unit/controllers/healthcheck/core.clj" (render "test/unit/controllers/healthcheck/core.clj" data)]]]
    (remove nil? files)))

(defn dashboards-files
  [data]
  [["dashboards/dashboard-loader.js" (render "dashboards/dashboard-loader.js" data)]
   ["dashboards/app-stats.json" (render "dashboards/app-stats.json" data)]])

(defn resources-files
  [data]
  [["resources/routes.txt" (render "resources/routes_site.txt")]])

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

(defn site-files
  [data args]
  (concat (src-files data)
          (test-files data args)
          (dashboards-files data)
          (resources-files data)
          (dev-files data)
          (project-files data)
          (controllers-files data args)
          (views-files data)
          (models-files data)
          (templates-files data)
          (public-files data)
          (component-files data args)))
