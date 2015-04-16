(ns leiningen.new.rents
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]])
  (:require [camel-snake-kebab.core :refer [->PascalCase]]
            [clojure.tools.cli :refer  [parse-opts]]
            [leiningen.new.api :refer [api-files]]
            [leiningen.new.site :refer [site-files]]
            [clojure.string :as string]
            [leiningen.new.common-api-templates :refer [api-var-map]]
            [leiningen.new.common-site-templates :refer [site-var-map]]
            [leiningen.new.templates :refer  [*force?*]]))

(def render (renderer "rents"))

(defn template-data
  [project-name ns-name var-map-fn options]
  (let [sanitized-ns-name (sanitize-ns ns-name)
        docker-name (string/replace name #"-" "")
        dockerised-svr (str (->PascalCase (sanitize-ns project-name)) "DevSvr")]
    (merge {:name project-name
            :ns-name sanitized-ns-name
            :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))
            :name-template "{{name}}"
            :location-template "{{location}}"
            :anti-forgery-field "{{{anti-forgery-field}}}"
            :title-template "{{title}}"
            :dockerised-svr dockerised-svr
            :project-root (str (:name options) "/")
            :sanitized-site (name-to-path (:site options))
            :sanitized-api  (name-to-path (:api options))}
          (var-map-fn sanitized-ns-name docker-name dockerised-svr options))))

(defn create-project
  [name ns-name files-fn var-map-fn options]
  (let [data (template-data name ns-name var-map-fn options)
        files (files-fn data options)]
     (apply ->files data files)))

(def cli-options
  [["-d" "--db DATABASE" "Database to be used. Currently only supports `mongodb`"
    :parse-fn keyword
    :default :mongodb
    :validate [#(= % :mongodb) "Currently only mongodb is currently supported"]]
   ["-s" "--site SITE" "Name of the site project"
    :default "site"]
   ["-a" "--api API" "Name of the api project"
    :default "api"]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> [""
        "A Leiningen template which produces a docker ready site or api with embedded"
        "Jetty web-server, Graphite/Grafanna instrumentation and many customisations."
        ""
        "Usage: lein new rents <project-name> <type> [options]"
        ""
        "Types:"
        "  site       Create a new site"
        "  api        Create a new web api"
        "  site+api   Create a new site with api back end"
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
   
     (case (first arguments)
       "api" (create-project name (:api options) api-files api-var-map options)
       "site" (create-project name (:site options) site-files site-var-map options)
       "site+api" (binding [*force?* true]
                    (do
                      (create-project name (:site options) site-files site-var-map options)
                      (create-project name (:api options) api-files api-var-map options)))
       (exit 1 (usage summary))))))
