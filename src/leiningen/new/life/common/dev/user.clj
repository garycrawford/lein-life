(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
    [clojure.tools.namespace.repl :refer [refresh]]
    [midje.repl :refer [autotest]]
    [reloaded.repl :refer [system init start stop go reset]]
    [cemerick.pomegranate :refer [add-dependencies]]
    [{{ns-name}}.components.system :refer [new-{{ns-name}}-system]]))

(reloaded.repl/set-init! new-{{ns-name}}-system)

(autotest)

(defn fix-exception
  []
  (require 'user :reload-all))

;; TODO: switch out for alembic as it allows reloading of project file

(defn add-dependency
  "Allows dynamic adding of dependencies to the classpath."
  [dependency version]
  (add-dependencies :coordinates  [[dependency version]]
                    :repositories {"clojars" "http://clojars.org/repo"
                                   "central" "http://repo1.maven.org/maven2/"}))
