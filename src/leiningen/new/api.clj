(ns leiningen.new.api
  (:require [leiningen.new.templates :refer [renderer]]
            [leiningen.new.life.customs :refer :all]
            [leiningen.new.db-template :refer :all]
            [leiningen.new.common-files :refer [render-common-files]]))

(def render (renderer "life"))

(defn render-api-files
  [data]
  [["{{sanitized-api}}/resources/routes.txt" (render "api/common/resources/routes.txt")]])

(defn files
  [data args]
  (concat (render-api-files data)
          (api-files args data)
          (render-common-files data "{{sanitized-api}}")))
