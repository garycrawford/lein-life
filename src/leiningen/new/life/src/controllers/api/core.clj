(ns {{ns-name}}.controllers.api.core
  (:require [{{ns-name}}.responses :refer [json-ok]]))

(defn entry-point
  []
  (json-ok {:msg "nice"}))
