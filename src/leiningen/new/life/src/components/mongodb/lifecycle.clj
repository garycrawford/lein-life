(ns {{ns-name}}.components.mongodb.lifecycle
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [taoensso.timbre :refer [info]]
            [robert.hooke :refer [prepend append]]
            [clojure.string :as string]))

(defn start
  [{:keys [conn] :as this}]
  (if conn
    this
    (let [uri (env :mongodb-uri)
          {:keys [db conn]} (mg/connect-via-uri uri)]
      (assoc this :db db :conn conn))))

(defn stop
  [{:keys [conn] :as this}]
  (if conn
   (do
     (mg/disconnect conn)
     (dissoc this :db :conn))
   this))

(defrecord MongoDB []
  component/Lifecycle
  (start [this]
    (start this))
  (stop [this]
    (stop this)))

(defn new-mongodb
  []
  (map->MongoDB {}))

(prepend start (info :mongodb :connecting))
(append  start (info :mongodb :connected))
(prepend stop  (info :mongodb :disconnecting))
(append  stop  (info :mongodb :disconnected))
