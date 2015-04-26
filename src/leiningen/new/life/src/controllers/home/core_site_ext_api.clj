(ns {{ns-name}}.controllers.home.core
  (:require [{{ns-name}}.views.home :refer [home-view]]
            [{{ns-name}}.responses :refer [model-view-ok]]
            [{{ns-name}}.platform.people-api.core :refer [get-people create-person get-person update-person delete-person]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.util.response :refer [redirect-after-post not-found]]))

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
  [{:keys [id]} template]
  (let [person (get-person id)
        view-model (person:m->vm person)]
    (model-view-ok {:model (add-anti-forgery view-model)
                    :view  (home-view template)})))

(defn home
  []
  (let [people (get-people) 
        view-model (people-list:m->vm people)]
      (model-view-ok {:model (add-anti-forgery view-model)
                      :view  (home-view "introduction")})))

(defn create-person-post
  [{:keys [name location]}]
  (create-person {:name name :location location})
  (redirect-after-post "/"))

(defn update-person-get
  [params]
  (person-response params "update-person"))

(defn update-person-post
  [{:keys [id name location]}]
  (update-person {:id id :name name :location location})
  (redirect-after-post "/"))

(defn delete-person-get
  [params]
  (person-response params "delete-person"))

(defn delete-person-post
  [{:keys [id]}]
  (delete-person id)
  (redirect-after-post "/"))
