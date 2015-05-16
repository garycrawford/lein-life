(ns leiningen.new.mongo-template
  (:require [leiningen.new.templates :refer [renderer *dir*]]
            [leiningen.new.db-template :refer :all]
            [stencil.core :as stencil]))

(defmethod db-site-files :mongodb
  [_ data]
  (let [render (renderer "life")]
    [["{{sanitized-site}}/test/{{sanitized-site}}/unit/components/mongodb/core.clj" (render "common/test/unit/components/mongodb/core.clj" data)]
     ["{{sanitized-site}}/test/{{sanitized-site}}/integration/controllers/home/core.clj" (render "site/test/integration/controllers/home/core.clj" data)]
     ["{{sanitized-site}}/test/{{sanitized-site}}/unit/controllers/home/core.clj" (render "site/test/unit/controllers/home/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/controllers/home/core.clj" (render "site/src/controllers/home/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/controllers/home/lifecycle.clj" (render "site/src/controllers/home/lifecycle.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/mongodb/core.clj" (render "site/src/components/mongodb/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/mongodb/lifecycle.clj" (render "site/src/components/mongodb/lifecycle.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/jetty/lifecycle.clj" (render "site/src/components/jetty/lifecycle.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/system.clj" (render "site/src/components/system.clj" data)]]))

(defmethod db-site-files :api
  [_ data]
  (let [render (renderer "life")]
    [["{{sanitized-site}}/test/{{sanitized-site}}/integration/controllers/home/core.clj" (render "site+api/test/integration/controllers/home/core.clj" data)]
     ["{{sanitized-site}}/test/{{sanitized-site}}/unit/controllers/home/core.clj" (render "site+api/test/unit/controllers/home/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/controllers/home/core.clj" (render "site+api/src/controllers/home/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/platform/people_api/core.clj" (render "site+api/src/platform/people_api/core.clj" data)]
     ["{{sanitized-site}}/test/{{sanitized-site}}/unit/platform/people_api/core.clj" (render "site+api/test/unit/platform/people_api/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/jetty/lifecycle.clj" (render "site+api/src/components/jetty/lifecycle.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/system.clj" (render "site+api/src/components/system.clj" data)]]))

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
