(ns {{ns-name}}.views.healthcheck
  (:require [clostache.parser :refer [render-resource]]
            [{{ns-name}}.views.shared :refer [wrap-with-layout]]))

(def healthcheck-path (partial format "templates/healthcheck/%s.mustache"))

(defn healthcheck-view
  [template]
  (let [path (healthcheck-path template)]
    {:fn (fn [model] (render-resource path model))
     :path path}))
