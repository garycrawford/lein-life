(ns {{ns-name}}.views.shared
  (:require [clostache.parser :refer [render-resource]]
            [clojure.java.io :as io]))

(def shared-path (partial format "templates/shared/%s.mustache"))

(defn slurp-shared
  [template]
  (slurp (io/resource (shared-path template))))

(def base-partials
  {:header (slurp-shared "header")
   :footer (slurp-shared "footer")})

(defn wrap-with-layout
  [title content]
  (render-resource (shared-path "default")
                   {:title   title
                    :content content}
                    base-partials))
