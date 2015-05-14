(ns leiningen.new.mongo-template
  (:require [leiningen.new.templates :refer [renderer *dir*]]
            [leiningen.new.db-template :refer :all]
            [stencil.core :as stencil]))

(defmethod db-site-files :mongodb
  [_ data]
  (let [render (renderer "life")]
    [["{{sanitized-site}}/test/{{sanitized-site}}/unit/components/mongodb/core.clj" (render "test/unit/components/mongodb/core.clj" data)]
     ["{{sanitized-site}}/test/{{sanitized-site}}/integration/controllers/home/core.clj" (render "test/integration/controllers/home/core_site_ext.clj" data)]
     ["{{sanitized-site}}/test/{{sanitized-site}}/unit/controllers/home/core.clj" (render "test/unit/controllers/home/core_site_ext.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/controllers/home/core.clj" (render "src/controllers/home/core_site_ext.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/controllers/home/lifecycle.clj" (render "src/controllers/home/lifecycle.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/mongodb/core.clj" (render "src/components/mongodb/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/mongodb/lifecycle.clj" (render "src/components/mongodb/lifecycle.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/jetty/lifecycle.clj" (render "src/components/jetty/lifecycle_site_ext.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/system.clj" (render "src/components/system_site_mongo.clj" data)]]))

(defmethod db-site-files :api
  [_ data]
  (let [render (renderer "life")]
    [["{{sanitized-site}}/test/{{sanitized-site}}/integration/controllers/home/core.clj" (render "test/integration/controllers/home/core_site_ext_api.clj" data)]
     ["{{sanitized-site}}/test/{{sanitized-site}}/unit/controllers/home/core.clj" (render "test/unit/controllers/home/core_site_ext_api.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/controllers/home/core.clj" (render "src/controllers/home/core_site_ext_api.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/platform/people_api/core.clj" (render "src/platform/people_api/core.clj" data)]
     ["{{sanitized-site}}/test/{{sanitized-site}}/unit/platform/people_api/core.clj" (render "test/unit/platform/people_api/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/jetty/lifecycle.clj" (render "src/components/jetty/lifecycle_site_int.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/system.clj" (render "src/components/system_site_api.clj" data)]]))

(defmethod db-dependencies :mongodb
  [_]
  ["[com.novemberain/monger \"2.1.0\"]"
   "[jstrutz/hashids \"1.0.1\"]"])

(defmethod db-dependencies :api
  [_]
  ["[clj-http \"1.1.2\" :exclusions [cheshire com.fasterxml.jackson.core/jackson-core]]"])

(defmethod db-environment-variables :mongodb
  [_]
  [":mongodb-uri \"mongodb://192.168.59.103/{{path}}\""])
