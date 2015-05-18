(ns leiningen.new.life
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]])
  (:require [clojure.tools.cli :refer  [parse-opts]]
            [leiningen.new.api :as api]
            [leiningen.new.site :as site]
            [clojure.string :as string]
            [camel-snake-kebab.core :refer [->PascalCase]]
            [leiningen.new.templates :refer  [*force?*]])
  (:import (java.net InetAddress)))

(def render (renderer "life"))

(def not-nil? (complement nil?))

(def cli-options
  [["-i" "--docker-ip DOCKER_IP" "IP address for Docker or boot2docker"
    :default "172.17.42.1"
    :validate [#(re-find #"\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\b" %) "Must be a valid ip"]]
   ["-d" "--db DATABASE" "Database to be used. Supports `mongodb` and `api`"
    :parse-fn keyword
    :default :mongodb
    :validate [#(or (= % :mongodb) (= % :api)) "Currently only mongodb or api are currently supported"]]
   ["-s" "--site-name SITE_NAME" "Name of the site project"
    :default "site"]
   ["-a" "--api-name API_NAME" "Name of the api project"
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
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (println "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (System/exit status))

(defn site-template-data
  [project-name ns-name options]
  (let [sanitized-ns-name (sanitize-ns ns-name)]
    (merge {:name project-name
            :ns-name sanitized-ns-name
            :api-ns-name (sanitize-ns (:api-name options))
            :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))
            :project-root (str project-name "/")
            :sanitized-site (name-to-path ns-name)
            :dockerised-svr (str (->PascalCase ns-name) "DevSvr")
            :name-template "{{name}}"
            :location-template "{{location}}"
            :anti-forgery-field "{{{anti-forgery-field}}}"
            :title-template "{{title}}"
            :docker-ip (:docker-ip options)}
          (site/site-var-map sanitized-ns-name options))))

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
            :sanitized-api  (name-to-path ns-name)
            :docker-ip (:docker-ip options)}
          (api/api-var-map sanitized-ns-name options))))

(defn api-vars
  [api-name]
  {:api-ns-name (sanitize-ns api-name)
   :api-docker-name (string/replace api-name "-" "")
   :api-dockerised-svr (str (->PascalCase api-name) "DevSvr")
   :api-path (string/replace api-name "-" "_")})

(defn site-vars
  [site-name]
  {:site-ns-name (sanitize-ns site-name)
   :site-docker-name (string/replace site-name "-" "")
   :site-dockerised-svr (str (->PascalCase site-name) "DevSvr")
   :site-path (string/replace site-name "-" "_")})

(defn site+api-vars
  [api-name site-name]
  (merge (api-vars api-name) (site-vars site-name)))

(defn db-name
  [parent-name]
  {:db-name (string/replace parent-name "-" "_")})

(defn create-api
  [parent-name {:keys [api-name] :as options}]
  (let [data (merge (api-template-data parent-name api-name options) (api-vars api-name) (db-name parent-name))
        files (api/files data options)]
     (apply ->files data files)))

(defn create-site
  [parent-name {:keys [site-name api-name db] :as options}]
  (binding  [*force?* true]
    (if (= db :api) (create-api parent-name (assoc options :db :mongodb)))
    (let [data (merge (site-template-data parent-name site-name options) (site+api-vars api-name site-name) (db-name parent-name))
          files (site/files data options)]
      (apply ->files data files))))

(defn create-projects
  [name template-type options summary]
  (case template-type
    "api" (create-api name options)
    "site" (create-site name options)
    (exit 1 (usage summary))))

(defn life
  [name & args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        template-type (first arguments)]
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
  
    (create-projects name template-type options summary)))
