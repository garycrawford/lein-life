(ns leiningen.new.life
  (:use [leiningen.new.templates :only [->files]])
  (:require [clojure.tools.cli :refer  [parse-opts]]
            [leiningen.new.api :as api]
            [leiningen.new.site :as site]
            [clojure.string :as string]
            [leiningen.new.templates :refer [*force?*]]))

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

(defn create-api
  [parent-name {:keys [api-name] :as options}]
  (let [data (api/api-template-data parent-name api-name options)
        files (api/files data options)]
     (apply ->files data files)))

(defn create-site
  [parent-name {:keys [site-name api-name db] :as options}]
  (binding  [*force?* true]
    (if (= db :api) (create-api parent-name (assoc options :db :mongodb)))
    (let [data (merge (api/api-template-data parent-name api-name options)
                      (site/site-template-data parent-name site-name options))
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
