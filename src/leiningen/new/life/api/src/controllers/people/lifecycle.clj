(ns {{ns-name}}.controllers.people.lifecycle
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :refer [info]]
            [robert.hooke :refer [prepend append]]))

(defn start
  [this]
  this)

(defn stop
  [this]
  this)

(defrecord PeopleController [mongodb]
  component/Lifecycle
  (start [this]
    (start this))
  (stop [this]
    (stop this)))

(defn new-people-controller
  []
  (map->PeopleController {}))

(prepend start (info :people-controller :starting))
(append  start (info :people-controller :started))
(prepend stop  (info :people-controller :stopping))
(append  stop  (info :people-controller :stopped))
