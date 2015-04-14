(ns {{ns-name}}.controllers.healthcheck.core
  (:require [{{ns-name}}.responses :refer [model-view-ok]]
            [clostache.parser :refer [render-resource]]))

(def healthcheck-path (partial format "templates/healthcheck/%s.mustache"))

(defn healthcheck-model
  [check-name check-status]
  {:name   check-name
   :status check-status})

(defn healthcheck-list-model
  []
  {:healthchecks [(healthcheck-model "service 1" "STARTING")
                  (healthcheck-model "service 2" "STARTED")
                  (healthcheck-model "service 3" "STOPPING")
                  (healthcheck-model "service 4" "STOPPED")
                  (healthcheck-model "service 5" "ERRORED")]})

(defn healthcheck-view
  [template]
  (let [path (healthcheck-path template)]
    {:fn (fn [model] (render-resource path model))
     :path path}))

(defn index
  []
  (model-view-ok {:model (healthcheck-list-model)
                  :view  (healthcheck-view "healthcheck-list")}))
