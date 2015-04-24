(ns {{ns-name}}.controllers.home.core
  (:require [{{ns-name}}.views.home :refer [home-view]]
            [{{ns-name}}.responses :refer [model-view-ok]]
            [clj-http.client :as client]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.util.response :refer [redirect-after-post]]
            [cheshire.core :refer [decode]]))

(defn parse-response
  [{:keys [body]}]
  (get (decode body true) :result))

(defn index-get
  []
  (let [response (client/get "http://192.168.59.103:4321/api/people")
        people (parse-response response)]
      (model-view-ok {:model {:anti-forgery-field (anti-forgery-field)
                              :people people}
                      :view  (home-view "introduction")})))

(defn index-post
  [{:keys [name location]}]
  (client/post "http://192.168.59.103:4321/api/people" {:form-params {:name name :location location}})
  (redirect-after-post "/"))
