(ns leiningen.new.site
  (:require [leiningen.new.templates :refer [renderer]]
            [leiningen.new.mongo-template :refer :all]
            [leiningen.new.api-template :refer :all]
            [leiningen.new.db-template :refer :all]
            [leiningen.new.common-files :refer [render-common-files]]))

(def render (renderer "life"))

(defn render-site-files
  [data]
  [["{{sanitized-site}}/resources/routes.txt" (render "site/common/resources/routes.txt")]
   ["{{sanitized-site}}/resources/templates/home/introduction.mustache" (render "site/common/resources/templates/home/introduction.mustache" data)]
   ["{{sanitized-site}}/resources/templates/home/add-person.mustache" (render "site/common/resources/templates/home/add-person.mustache" data)]
   ["{{sanitized-site}}/resources/templates/home/delete-person.mustache" (render "site/common/resources/templates/home/delete-person.mustache" data)]
   ["{{sanitized-site}}/resources/templates/home/update-person.mustache" (render "site/common/resources/templates/home/update-person.mustache" data)]
   ["{{sanitized-site}}/resources/templates/home/person-list.mustache" (render "site/common/resources/templates/home/person-list.mustache" data)]
   ["{{sanitized-site}}/resources/templates/home/not-found.mustache" (render "site/common/resources/templates/home/not-found.mustache" data)]
   ["{{sanitized-site}}/src/{{sanitized-site}}/views/home.clj" (render "site/common/src/views/home.clj" data)]])

(defn site-files
  [data args]
  (concat (render-site-files data)
          (db-site-files args data)
          (render-common-files data "{{sanitized-site}}")))
