(ns {{ns-name}}.controllers.home.lifecycle
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :refer [info]]
            [robert.hooke :refer [prepend append]]))

(defn start
  [this]
  this)

(defn stop
  [this]
  this)

(defrecord HomeController [mongodb]
  component/Lifecycle
  (start [this]
    (start this))
  (stop [this]
    (stop this)))

(defn new-home-controller
  []
  (map->HomeController {}))

(prepend start (info :home-controller :starting))
(append  start (info :home-controller :started))
(prepend stop  (info :home-controller :stopping))
(append  stop  (info :home-controller :stopped))
