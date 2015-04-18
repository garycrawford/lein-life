(ns leiningen.new.life
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]])
  (:require [camel-snake-kebab.core :refer [->PascalCase]]
            [clojure.tools.cli :refer  [parse-opts]]
            [leiningen.new.api :refer [api-files]]
            [leiningen.new.site :refer [site-files]]
            [clojure.string :as string]
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
  (println msg)
  (System/exit status))

(defn template-data
  [project-name ns-name var-map-fn options]
  (let [sanitized-ns-name (sanitize-ns ns-name)
        docker-name (string/replace project-name #"-" "")
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
            :docker-site-name (string/replace (:site options) #"-" "")
            :docker-api-name (string/replace (:api options) #"-" "")
            :sanitized-site (name-to-path (:site options))
            :sanitized-api  (name-to-path (:api options))}
          (var-map-fn sanitized-ns-name docker-name dockerised-svr options))))

(defn site-template-data
  [project-name ns-name options]
  (let [sanitized-ns-name (sanitize-ns ns-name)
        docker-name (string/replace sanitized-ns-name #"-" "")
        dockerised-svr (str (->PascalCase sanitized-ns-name) "DevSvr")]
    (merge {:name project-name
            :ns-name sanitized-ns-name
            :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))
            :dockerised-svr dockerised-svr
            :project-root (str project-name "/")
            :docker-site-name (string/replace ns-name #"-" "")
            :sanitized-site (name-to-path ns-name)
            :name-template "{{name}}"
            :location-template "{{location}}"
            :anti-forgery-field "{{{anti-forgery-field}}}"
            :title-template "{{title}}"}
          (site-var-map sanitized-ns-name docker-name dockerised-svr options))))

(defn names
  [ns-name]
  (let [sanitized-name (sanitize-ns ns-name)]
    {:docker-name (string/replace sanitized-name #"-" "")
     :ns-name sanitized-name
     :dockerised-svr (str (->PascalCase sanitized-name) "DevSvr")}))

(defn api-template-data
  [project-name ns-name options]
  (let [sanitized-name (sanitize-ns ns-name)
        docker-name (string/replace sanitized-name #"-" "")
        dockerised-svr (str (->PascalCase sanitized-name) "DevSvr")]
    (merge {:name project-name
            :ns-name sanitized-name
            :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))
            :dockerised-svr dockerised-svr
            :project-root (str project-name "/")
            :docker-api-name (string/replace ns-name #"-" "")
            :sanitized-api  (name-to-path ns-name)}
          (api-var-map sanitized-name docker-name dockerised-svr options))))

(defn create-site
  ([parent-name options] (create-site parent-name options nil))
  ([parent-name {:keys [site api] :as options} add-api-dep?]
   (let [data (site-template-data parent-name site options)
         files (site-files data options)
         compose-path (str parent-name "/docker-compose.yml")
         compose-site-content (compose-site-proj (merge (names site) (when add-api-dep? {:api-docker-name api})) options)]
      (apply ->files data files)
      (spit compose-path compose-site-content :append true))))

(defn create-api
  [parent-name {:keys [api] :as options}]
  (let [data (api-template-data parent-name api options)
        files (api-files data options)
        compose-path (str parent-name "/docker-compose.yml")
        compose-api-content (compose-api-proj (names api) options)]
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
