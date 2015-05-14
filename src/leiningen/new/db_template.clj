(ns leiningen.new.db-template)

(defmulti db-environment-variables :db)
(defmulti db-dependencies :db)
(defmulti db-files :db)
(defmulti db-api-files :db)
(defmulti db-site-files :db)

(defmethod db-environment-variables :default
  [_]
  [])
