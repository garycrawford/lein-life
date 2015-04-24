(ns {{ns-name}}.controllers.home.core
  (:require [{{ns-name}}.views.home :refer [home-view]]
            [{{ns-name}}.responses :refer [model-view-ok]]
            [{{ns-name}}.components.mongodb.core :refer [find-by-query insert]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.util.response :refer [redirect-after-post]]))

(defn index-get
  [{:keys [mongodb]}]
  (let [people (find-by-query mongodb "people" {})]
    (model-view-ok {:model {:anti-forgery-field (anti-forgery-field)
                            :people people}
                    :view  (home-view "introduction")})))

(defn index-post
  [{:keys [mongodb]} {:keys [name location]}]
  (insert mongodb "people" {:name name :location location})
  (redirect-after-post "/"))
