(ns {{ns-name}}.controllers.people.core
  (:require [{{ns-name}}.models.home :refer [about-model about-model!]]
            [{{ns-name}}.responses :refer [json-ok]]
            [ring.util.response :refer [not-found redirect-after-post]]))

(defn list-people
  []
  (json-ok []))

(defn create-person
  [params]
  (json-ok {:id 0}))

(defn read-person
  [id]
  (json-ok {:name "Gary" :location "Scotland"}))

(defn update-person
  [params]
  (json-ok {:id 0}))

(defn delete-person
  [id]
  (json-ok {:id 0}))

(defn index-get
  []
  (if-let [model (about-model)]
    (json-ok model) 
    (not-found {})))

(defn index-post
  [{:keys [name location]}]
  (about-model! {:name name :location location})
  (redirect-after-post "/"))
