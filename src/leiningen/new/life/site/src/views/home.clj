(ns {{ns-name}}.views.home
  (:require [clostache.parser :refer [render-resource]]
            [{{ns-name}}.views.shared :refer [wrap-with-layout]]
            [clojure.java.io :as io]))

(def home-path (partial format "templates/home/%s.mustache"))

;(defn about-view
;  [model]
;  (let [content (render-resource (home-path "about") model)]
;    (wrap-with-layout "home" content)))

(def person-list (slurp (io/resource (home-path "person-list"))))
(def add-person (slurp (io/resource (home-path "add-person"))))
(def home-view-partials {:person-list person-list
                         :add-person add-person})

(defn home-view
  [template]
  (let [path (home-path template)]
    {:fn (fn [model] (render-resource path model home-view-partials))
     :path path}))
