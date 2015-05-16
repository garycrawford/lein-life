(ns {{ns-name}}.controllers.healthcheck.lifecycle
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :refer [info]]
            [robert.hooke :refer  [prepend append]]))

(defn start
  [this]
  this)

(defn stop
  [this]
  this)

(defrecord HealthcheckController []
  component/Lifecycle
  (start [this]
    (start this))
  (stop [this]
    (stop this)))

(defn new-healthcheck-controller
  []
  (map->HealthcheckController {}))

(prepend start (info :healthcheck-controller :starting))
(append  start (info :healthcheck-controller :started))
(prepend stop  (info :healthcheck-controller :stopping))
(append  stop  (info :healthcheck-controller :stopped))
