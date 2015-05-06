(ns leiningen.new.life
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]])
  (:require [clojure.tools.cli :refer  [parse-opts]]
            [leiningen.new.api :refer [api-files]]
            [leiningen.new.site :refer [site-files]]
            [clojure.string :as string]
            [camel-snake-kebab.core :refer [->PascalCase]]
            [leiningen.new.common-api-templates :refer [api-var-map]]
            [leiningen.new.common-site-templates :refer [site-var-map]]
            [leiningen.new.templates :refer  [*force?*]]
            [leiningen.new.common-templates :refer [compose-api-proj compose-site-proj compose-deps]]))

(def render (renderer "life"))

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
        "Usage: lein new life <project-name> <type> [options]"
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
  (System/exit status))

(defn site-template-data
  [project-name ns-name options]
  (let [sanitized-ns-name (sanitize-ns ns-name)]
    (merge {:name project-name
            :ns-name sanitized-ns-name
            :api-ns-name (sanitize-ns (:api options))
            :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))
            :project-root (str project-name "/")
            :sanitized-site (name-to-path ns-name)
            :dockerised-svr (str (->PascalCase ns-name) "DevSvr")
            :name-template "{{name}}"
            :location-template "{{location}}"
            :anti-forgery-field "{{{anti-forgery-field}}}"
            :title-template "{{title}}"}
          (site-var-map sanitized-ns-name options))))

(defn names
  [parent-name ns-name](string/replace ns-name "-" "_")
  {:parent-name parent-name
   :ns-name (sanitize-ns ns-name)})

(defn api-template-data
  [project-name ns-name options]
  (let [sanitized-ns-name (sanitize-ns ns-name)]
    (merge {:name project-name
            :ns-name sanitized-ns-name
            :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))
            :project-root (str project-name "/")
            :dockerised-svr (str (->PascalCase ns-name) "DevSvr")
            :sanitized-api  (name-to-path ns-name)}
          (api-var-map sanitized-ns-name options))))

(defn create-site
  ([parent-name options] (create-site parent-name options nil))
  ([parent-name {:keys [site api] :as options} add-api-dep?]
   (let [opts (merge options (when add-api-dep? {:db :api}))
         data (site-template-data parent-name site opts)
         files (site-files data opts)
         compose-path (str parent-name "/docker-compose.yml")
         compose-site-content (compose-site-proj
                                (merge (names parent-name site)
                                       (when add-api-dep? {:api-name (sanitize-ns api)}))
                                opts)]
      (apply ->files data files)
      (spit compose-path compose-site-content :append true))))

(defn create-api
  [parent-name {:keys [api] :as options}]
  (let [data (api-template-data parent-name api options)
        files (api-files data options)
        compose-path (str parent-name "/docker-compose.yml")
        compose-api-content (compose-api-proj (names parent-name api) options)]
     (apply ->files data files)
     (spit compose-path compose-api-content :append true)))

(defn create-site+api
  [parent-name options]
  (binding [*force?* true]
    (do
      (create-site parent-name options true)
      (create-api parent-name options))))

(defn create-projects
  [name template-type options summary]
  (case template-type
    "api" (create-api name options)
    "site" (create-site name options)
    "site+api" (create-site+api name options)
    (exit 1 (usage summary)))

  (spit (str name "/docker-compose.yml") (compose-deps options) :append true))

(defn life
  [name & args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        template-type (first arguments)]
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
  
    (create-projects name template-type options summary)))
