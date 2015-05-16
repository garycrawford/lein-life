(ns leiningen.new.api
  (:require [leiningen.new.templates :refer [renderer]]
            [leiningen.new.mongo-template :refer :all]
            [leiningen.new.db-template :refer :all]
            [leiningen.new.common-files :refer [render-common-files]]))

(def render (renderer "life"))

(defn render-api-files
  [data]
  [["{{sanitized-api}}/resources/routes.txt" (render "api/resources/routes.txt")]])

(defn api-files
  [data args]
  (concat (render-api-files data)
          (db-api-files args data)
          (render-common-files data "{{sanitized-api}}")))
