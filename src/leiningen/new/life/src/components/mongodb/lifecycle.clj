(ns {{ns-name}}.components.mongodb.lifecycle
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [taoensso.timbre :refer [info]]
            [robert.hooke :refer [prepend append]]
            [clojure.string :as string]))

(defn update-db-name
  [uri db-name]
  (let [slash-index (.lastIndexOf uri "/")]
    (-> uri
        (subs 0 slash-index)
        (str "/" db-name))))

(defn mongo-uri
  [{:keys [db-name]}]
  (let [uri (env :mongodb-uri)]
    (if (= db-name :from-config)
      uri
      (update-db-name uri db-name))))

(defn start
  [{:keys [conn] :as this}]
  (if conn
    this
    (let [uri (mongo-uri this)
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
  ([] (new-mongodb :from-config))
  ([db-name] (map->MongoDB {:db-name db-name})))

(prepend start (info :mongodb :connecting))
(append  start (info :mongodb :connected))
(prepend stop  (info :mongodb :disconnecting))
(append  stop  (info :mongodb :disconnected))
