(ns leiningen.new.common-files
  (:require [leiningen.new.templates :refer [renderer]]))

(def render (renderer "life"))

(defn render-common-files
  [data sanitized-name]
  (let [root-path-fn (partial str sanitized-name "/")
        src-path-fn (partial str sanitized-name "/src/" sanitized-name "/")
        test-path-fn (partial str sanitized-name "/test/" sanitized-name "/")
        templates-path-fn (partial str sanitized-name "/resources/templates/")]
    [[(src-path-fn "components/graphite/lifecycle.clj") (render "common/src/components/graphite/lifecycle.clj" data)]
     [(src-path-fn "controllers/healthcheck/lifecycle.clj") (render "common/src/controllers/healthcheck/lifecycle.clj" data)]
     [(src-path-fn "controllers/healthcheck/core.clj") (render "common/src/controllers/healthcheck/core.clj" data)]
     [(src-path-fn "views/healthcheck.clj") (render "common/src/views/healthcheck.clj" data)]
     [(src-path-fn "models/healthcheck.clj") (render "common/src/models/healthcheck.clj" data)]
     [(src-path-fn "views/shared.clj") (render "common/src/views/shared.clj" data)]
     [(templates-path-fn "healthcheck/healthcheck-list.mustache") (render "common/resources/templates/healthcheck/healthcheck-list.mustache" data)]
     [(templates-path-fn "shared/default.mustache") (render "common/resources/templates/shared/default.mustache" data)]
     [(templates-path-fn "shared/header.mustache") (render "common/resources/templates/shared/header.mustache" data)]
     [(templates-path-fn "shared/footer.mustache") (render "common/resources/templates/shared/footer.mustache" data)]
     [(src-path-fn "zygote.clj") (render "common/src/zygote.clj" data)]
     [(src-path-fn "logging_config.clj") (render "common/src/logging_config.clj" data)]
     [(src-path-fn "responses.clj") (render "common/src/responses.clj" data)]
     [(test-path-fn "checkers/core.clj") (render "common/test/checkers/core.clj" data)]
     [(test-path-fn "unit/components/graphite/lifecycle.clj") (render "common/test/unit/components/graphite/lifecycle.clj" data)]
     [(test-path-fn "unit/controllers/healthcheck/core.clj") (render "common/test/unit/controllers/healthcheck/core.clj" data)]
     ["dashboards/dashboard-loader.js" (render "common/dashboards/dashboard-loader.js" data)]
     ["dashboards/app-stats.json" (render "common/dashboards/site-stats.json" data)]
     [(root-path-fn "resources/public/css/styles.css") (render "common/resources/public/css/styles.css" data)]
     [(root-path-fn "dev/user.clj") (render "common/dev/user.clj" data)]
     [(root-path-fn "project.clj") (render "common/project.clj" data)]
     [(root-path-fn "profiles.clj") (render "common/profiles.clj" data)]
     [(root-path-fn "Dockerfile") (render "common/Dockerfile" data)]
     [(root-path-fn ".dockerignore") (render "common/dockerignore" data)]
     [(root-path-fn ".gitignore") (render "common/gitignore" data)]
     [(root-path-fn ".midje.clj") (render "common/midje.clj" data)]
     [(root-path-fn "README.md") (render "common/README.md" data)]]))
