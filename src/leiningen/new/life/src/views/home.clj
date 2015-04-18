(ns {{ns-name}}.views.home
  (:require [clostache.parser :refer [render-resource]]
            [{{ns-name}}.views.shared :refer [wrap-with-layout]]
          ; [clojure.java.io :as io]
            ))

(def home-path (partial format "templates/home/%s.mustache"))

;(defn slurp-home
;  [template]
;  (slurp (io/resource (home-path template))))
;
;(defn about-view
;  [model]
;  (let [content (render-resource (home-path "about") model)]
;    (wrap-with-layout "home" content)))

(defn home-view
  [template]
  (let [path (home-path template)]
    {:fn (fn [model] (render-resource path model))
     :path path}))
