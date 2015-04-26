(ns {{ns-name}}.controllers.home.core
  (:require [{{ns-name}}.views.home :refer [home-view]]
            [{{ns-name}}.responses :refer [model-view-200 model-view-404]]
            [{{ns-name}}.components.mongodb.core :refer [find-by-query find-by-id insert update delete]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.util.response :refer [redirect-after-post]]))

(defn person:m->vm
  "Converts a person model into a person view-model"
  [model]
  (select-keys model [:name :location :id]))

(defn people-list:m->vm
  "Converts a poeple-list model into a person-list view-model"
  [model]
  {:people (map person:m->vm model)})

(defn add-anti-forgery
  "Adds an anti-forgery token to a model map"
  [model]
  (merge model
         {:anti-forgery-field (anti-forgery-field)}))

(defn person-response
  "Builds a response based on the named template
   and the person data associated with the id"
  [{:keys [mongodb]} {:keys [id]} template]
  (if-let [person (find-by-id mongodb "people" id)]
    (model-view-200 {:model (add-anti-forgery (person:m->vm person))
                     :view  (home-view template)})
    (model-view-404 {:model {}
                     :view  (home-view "not-found")})))

(defn home
  [{:keys [mongodb]}]
  (let [people (find-by-query mongodb "people" {})
        view-model (people-list:m->vm people)]
    (model-view-200 {:model (add-anti-forgery view-model)
                     :view  (home-view "introduction")})))

(defn create-person-post
  [{:keys [mongodb]} {:keys [name location]}]
  (insert mongodb "people" {:name name :location location})
  (redirect-after-post "/"))

(defn update-person-get
  [component params]
  (person-response component params "update-person"))

(defn update-person-post
  [{:keys [mongodb]} {:keys [id name location]}]
  (update mongodb "people" {:id id :name name :location location})
  (redirect-after-post "/"))

(defn delete-person-get
  [component params]
  (person-response component params "delete-person"))

(defn delete-person-post
  [{:keys [mongodb]} {:keys [id]}]
  (delete mongodb "people" id)
  (redirect-after-post "/"))
