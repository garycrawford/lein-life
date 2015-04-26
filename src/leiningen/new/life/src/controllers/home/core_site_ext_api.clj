(ns {{ns-name}}.controllers.home.core
  (:require [{{ns-name}}.views.home :refer [home-view]]
            [{{ns-name}}.responses :refer [model-view-ok]]
            [clj-http.client :as client]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.util.response :refer [redirect-after-post]]
            [cheshire.core :refer [decode]]))

(defn person:m->vm
  "Converts a person model into a person view-model"
  [model]
  (select-keys model [:name :location :id]))

(defn people-list:m->vm
  "Converts a poeple-list model into a person-list view-model"
  [model]
  {:people (map person:m->vm model)})

(defn parse-people-list-response
  "Extracts the result of an http response containing a people list and converts into a clojure data structure"
  [{:keys [body]}]
  (get (decode body true) :result))

(defn person-by-id-uri
  "Creates a URI to obtain person information from a person id"
  [id]
  (str "http://192.168.59.103:4321/api/people/" id))

(defn add-anti-forgery
  "Adds an anti-forgery token to a model map"
  [model]
  (merge model
         {:anti-forgery-field (anti-forgery-field)}))

(defn parse-person-response
  "Extracts the result of an http response containing a single person and converts into a clojure data structure"
  [{:keys [body]}]
  (get (decode body true) :result))

(defn home
  []
  (let [response (client/get "http://192.168.59.103:4321/api/people")
        people (parse-people-list-response response)
        view-model (people-list:m->vm people)]
      (model-view-ok {:model (add-anti-forgery view-model)
                      :view  (home-view "introduction")})))

(defn create-person
  [{:keys [name location]}]
  (client/post "http://192.168.59.103:4321/api/people" {:form-params {:name name :location location}})
  (redirect-after-post "/"))

(defn update-person-get
  [{:keys [id]}]
  (let [view-model (-> id
                       person-by-id-uri
                       client/get
                       parse-person-response
                       person:m->vm)]
    (model-view-ok {:model (add-anti-forgery view-model)
                    :view  (home-view "update-person")})))

(defn update-person-post
  [{:keys [id name location]}]
  (let [uri (person-by-id-uri id)]
    (client/put uri {:form-params {:id id :name name :location location}}))
  (redirect-after-post "/"))

(defn delete-person-get
  [{:keys [id]}]
  (let [view-model (-> id
                       person-by-id-uri
                       client/get
                       parse-person-response
                       person:m->vm)]
    (model-view-ok {:model (add-anti-forgery view-model)
                    :view  (home-view "delete-person")})))

(defn delete-person-post
  [{:keys [id]}]
  (let [uri (person-by-id-uri id)]
    (client/delete uri {:form-params {:id id}}))
  (redirect-after-post "/"))
