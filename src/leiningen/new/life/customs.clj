(ns leiningen.new.life.customs
  (:require [leiningen.new.templates :refer [renderer]]
            [leiningen.new.db-template :refer :all]))

(defmethod api-files :mongodb
  [_ data]
  (let [render (renderer "life")]
    [["{{sanitized-api}}/src/{{sanitized-api}}/controllers/people/lifecycle.clj" (render "api/mongodb/src/controllers/people/lifecycle.clj" data)]
     ["{{sanitized-api}}/src/{{sanitized-api}}/controllers/people/core.clj" (render "api/mongodb/src/controllers/people/core.clj" data)]
     ["{{sanitized-api}}/test/{{sanitized-api}}/integration/controllers/people/core.clj" (render "api/mongodb/test/integration/controllers/people/core.clj" data)]
     ["{{sanitized-api}}/test/{{sanitized-api}}/unit/controllers/people/core.clj" (render "api/mongodb/test/unit/controllers/people/core.clj" data)]
     ["{{sanitized-api}}/src/{{sanitized-api}}/components/mongodb/lifecycle.clj" (render "api/mongodb/src/components/mongodb/lifecycle.clj" data)]
     ["{{sanitized-api}}/src/{{sanitized-api}}/components/mongodb/core.clj" (render "api/mongodb/src/components/mongodb/core.clj" data)]
     ["{{sanitized-api}}/test/{{sanitized-api}}/unit/components/mongodb/core.clj" (render "api/mongodb/test/unit/components/mongodb/core.clj" data)]
     ["{{sanitized-api}}/src/{{sanitized-api}}/components/jetty/lifecycle.clj" (render "api/mongodb/src/components/jetty/lifecycle.clj" data)]
     ["{{sanitized-api}}/src/{{sanitized-api}}/components/system.clj" (render "api/mongodb/src/components/system.clj" data)]
     ["docker-compose.yml" (render "api/mongodb/docker-compose.yml" data)]]))

(defmethod site-files :api
  [_ data]
  (let [render (renderer "life")]
    [["{{sanitized-site}}/test/{{sanitized-site}}/integration/controllers/home/core.clj" (render "site/api/test/integration/controllers/home/core.clj" data)]
     ["{{sanitized-site}}/test/{{sanitized-site}}/unit/controllers/home/core.clj" (render "site/api/test/unit/controllers/home/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/controllers/home/core.clj" (render "site/api/src/controllers/home/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/platform/people_api/core.clj" (render "site/api/src/platform/people_api/core.clj" data)]
     ["{{sanitized-site}}/test/{{sanitized-site}}/unit/platform/people_api/core.clj" (render "site/api/test/unit/platform/people_api/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/jetty/lifecycle.clj" (render "site/api/src/components/jetty/lifecycle.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/system.clj" (render "site/api/src/components/system.clj" data)]
     ["docker-compose.yml" (render "site/api/docker-compose.yml" data)]]))

(defmethod dependencies :api
  [_]
  ["[clj-http \"1.1.2\" :exclusions [cheshire com.fasterxml.jackson.core/jackson-core]]"])

(defmethod site-files :mongodb
  [_ data]
  (let [render (renderer "life")]
    [["{{sanitized-site}}/test/{{sanitized-site}}/unit/components/mongodb/core.clj" (render "site/mongodb/test/unit/components/mongodb/core.clj" data)]
     ["{{sanitized-site}}/test/{{sanitized-site}}/integration/controllers/home/core.clj" (render "site/mongodb/test/integration/controllers/home/core.clj" data)]
     ["{{sanitized-site}}/test/{{sanitized-site}}/unit/controllers/home/core.clj" (render "site/mongodb/test/unit/controllers/home/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/controllers/home/core.clj" (render "site/mongodb/src/controllers/home/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/controllers/home/lifecycle.clj" (render "site/mongodb/src/controllers/home/lifecycle.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/mongodb/core.clj" (render "site/mongodb/src/components/mongodb/core.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/mongodb/lifecycle.clj" (render "site/mongodb/src/components/mongodb/lifecycle.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/jetty/lifecycle.clj" (render "site/mongodb/src/components/jetty/lifecycle.clj" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/components/system.clj" (render "site/mongodb/src/components/system.clj" data)]
     ["docker-compose.yml" (render "site/mongodb/docker-compose.yml" data)]]))

(defmethod dependencies :mongodb
  [_]
  ["[com.novemberain/monger \"2.1.0\"]"
   "[jstrutz/hashids \"1.0.1\"]"])

(defmethod environment-variables :mongodb
  [_]
  [":mongodb-uri \"mongodb://192.168.59.103/{{path}}\""])
