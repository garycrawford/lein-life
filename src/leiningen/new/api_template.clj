(ns leiningen.new.api-template
  (:require [leiningen.new.templates :refer [renderer]]
            [leiningen.new.db-template :refer :all]))

(defmethod db-api-files :mongodb
  [_ data]
  (let [render (renderer "life")]
    [["{{sanitized-api}}/src/{{sanitized-api}}/controllers/people/lifecycle.clj" (render "api/src/controllers/people/lifecycle.clj" data)]
     ["{{sanitized-api}}/src/{{sanitized-api}}/controllers/people/core.clj" (render "api/src/controllers/people/core.clj" data)]
     ["{{sanitized-api}}/test/{{sanitized-api}}/integration/controllers/people/core.clj" (render "api/test/integration/controllers/people/core.clj" data)]
     ["{{sanitized-api}}/test/{{sanitized-api}}/unit/controllers/people/core.clj" (render "api/test/unit/controllers/people/core.clj" data)]
     ["{{sanitized-api}}/src/{{sanitized-api}}/components/mongodb/lifecycle.clj" (render "api/src/components/mongodb/lifecycle.clj" data)]
     ["{{sanitized-api}}/src/{{sanitized-api}}/components/mongodb/core.clj" (render "api/src/components/mongodb/core.clj" data)]
     ["{{sanitized-api}}/test/{{sanitized-api}}/unit/components/mongodb/core.clj" (render "common/test/unit/components/mongodb/core.clj" data)]
     ["{{sanitized-api}}/src/{{sanitized-api}}/components/jetty/lifecycle.clj" (render "api/src/components/jetty/lifecycle.clj" data)]
     ["{{sanitized-api}}/src/{{sanitized-api}}/components/system.clj" (render "api/src/components/system.clj" data)]]))
