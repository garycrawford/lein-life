(ns {{ns-name}}.models.healthcheck)

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
