(ns {{ns-name}}.controllers.healthcheck.core
  (:require [{{ns-name}}.models.healthcheck :refer [healthcheck-list-model]]
            [{{ns-name}}.views.healthcheck :refer [healthcheck-view]]
            [{{ns-name}}.responses :refer [model-view-200]]))

(defn index
  []
  (model-view-200 {:model (healthcheck-list-model)
                   :view  (healthcheck-view "healthcheck-list")}))
