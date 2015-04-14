(ns leiningen.new.rents
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]])
  (:require [camel-snake-kebab.core :refer [->PascalCase]]
            [clojure.tools.cli :refer  [parse-opts]]
            [leiningen.new.api :refer [api-files]]
            [leiningen.new.site :refer [site-files]]
            [clojure.string :as string]
            [leiningen.new.common-api-templates :refer [api-var-map]]
            [leiningen.new.common-site-templates :refer [site-var-map]]))

(def render (renderer "rents"))

(defn template-data
  [name var-map-fn options]
  (let [ns-name (sanitize-ns name)
        docker-name (string/replace name #"-" "")
        dockerised-svr (str (->PascalCase (sanitize-ns name)) "DevSvr")]
    (merge {:name name
            :ns-name ns-name
            :sanitized (name-to-path name)
            :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))
            :name-template "{{name}}"
            :location-template "{{location}}"
            :anti-forgery-field "{{{anti-forgery-field}}}"
            :title-template "{{title}}"
            :dockerised-svr dockerised-svr}
          (var-map-fn ns-name docker-name dockerised-svr options))))

(defn create-project
  [name files-fn var-map-fn options]
  (let [data (template-data name var-map-fn options)
        files (files-fn data options)]
     (apply ->files data files)))

(def cli-options
  [["-d" "--db DATABASE" "Database to be used. Currently only supports `mongodb`"
    :parse-fn keyword
    :validate [#(= % :mongodb) "Currently only mongodb is currently supported"]]
   ["-v" nil "Verbosity level; may be specified multiple times to increase value"
    ;; If no long-option is specified, an option :id must be given
    :id :verbosity
    :default 0
    ;; Use assoc-fn to create non-idempotent options
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> [""
        "A Leiningen template which produces a docker ready site or api with embedded"
        "Jetty web-server, Graphite/Grafanna instrumentation and many customisations."
        ""
        "Usage: lein new rents <project-name> <type> [options]"
        ""
        "Types:"
        "  api      Create a new web api"
        "  site     Create a new site"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn rents
  ([name] (rents name "--help"))
  ([name & args]
   (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
     (cond
       (:help options) (exit 0 (usage summary))
       (not= (count arguments) 1) (exit 1 (usage summary))
       errors (exit 1 (error-msg errors)))
   
     ;; Execute program with options
     (case (first arguments)
       "api" (create-project name api-files api-var-map options)
       "site" (create-project name site-files site-var-map options)
       (exit 1 (usage summary))))))
