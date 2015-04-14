(ns {{ns-name}}.controllers.home.core
  (:require [{{ns-name}}.models.home :refer [about-model about-model!]]
            [{{ns-name}}.views.home :refer [home-view]]
            [{{ns-name}}.responses :refer [model-view-ok]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.util.response :refer [redirect-after-post]]))

(defn index-get
  []
  (if-let [result (about-model)]
    (model-view-ok {:model result
                    :view  (home-view "welcome")})   
    (model-view-ok {:model {:anti-forgery-field (anti-forgery-field)}
                    :view  (home-view "introduction")})))

(defn index-post
  [{:keys [name location]}]
  (about-model! {:name name :location location})
  (redirect-after-post "/"))
