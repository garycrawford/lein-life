(ns {{ns-name}}.zygote
  (:require [com.stuartsierra.component :as component]
            [{{ns-name}}.components.system :as system])
  (:gen-class))

(def {{ns-name}}-system (system/new-{{ns-name}}-system))

(defn -main
  "The entry point for the application."
  [& args]
  (alter-var-root #'{{ns-name}}-system component/start))
