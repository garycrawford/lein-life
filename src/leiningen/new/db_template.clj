(ns leiningen.new.db-template)

(defmulti api-files :db)
(defmulti site-files :db)
(defmulti dependencies :db)
(defmulti environment-variables :db)

(defmethod api-files :default
  [_ _]
  [])

(defmethod site-files :default
  [_ _]
  [])

(defmethod dependencies :default
  [_]
  [])

(defmethod environment-variables :default
  [_]
  [])
