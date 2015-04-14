(ns {{ns-name}}.controllers.home.core
  (:require [{{ns-name}}.views.home :refer [home-view]]
            [{{ns-name}}.responses :refer [model-view-ok]]
            [{{ns-name}}.components.mongodb.core :refer [find-one-by-query insert]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.util.response :refer [redirect-after-post]]))

(defn index-get
  [{:keys [mongodb]}]
  (if-let [db-model (find-one-by-query mongodb "visitors" {})]
    (model-view-ok {:model (select-keys db-model [:name :location])
                    :view  (home-view "welcome")})   
    (model-view-ok {:model {:anti-forgery-field (anti-forgery-field)}
                    :view  (home-view "introduction")})))

(defn index-post
  [{:keys [mongodb]} {:keys [name location]}]
  (insert mongodb "visitors" {:name name :location location})
  (redirect-after-post "/"))
