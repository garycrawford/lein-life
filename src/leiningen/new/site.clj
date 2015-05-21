(ns leiningen.new.site
  (:require [clojure.string :as string]
            [clostache.parser :refer [render]]
            [camel-snake-kebab.core :refer [->PascalCase]]
            [leiningen.new.templates :refer [renderer name-to-path sanitize-ns]]
            [leiningen.new.life.customs :refer :all]
            [leiningen.new.common :refer [render-common-files]]))

(defn render-site-files
  [data]
  (let [render (renderer "life")]
    [["{{sanitized-site}}/resources/routes.txt" (render "site/common/resources/routes.txt")]
     ["{{sanitized-site}}/resources/templates/home/introduction.mustache" (render "site/common/resources/templates/home/introduction.mustache" data)]
     ["{{sanitized-site}}/resources/templates/home/add-person.mustache" (render "site/common/resources/templates/home/add-person.mustache" data)]
     ["{{sanitized-site}}/resources/templates/home/delete-person.mustache" (render "site/common/resources/templates/home/delete-person.mustache" data)]
     ["{{sanitized-site}}/resources/templates/home/update-person.mustache" (render "site/common/resources/templates/home/update-person.mustache" data)]
     ["{{sanitized-site}}/resources/templates/home/person-list.mustache" (render "site/common/resources/templates/home/person-list.mustache" data)]
     ["{{sanitized-site}}/resources/templates/home/not-found.mustache" (render "site/common/resources/templates/home/not-found.mustache" data)]
     ["{{sanitized-site}}/src/{{sanitized-site}}/views/home.clj" (render "site/common/src/views/home.clj" data)]]))

(defn files
  [data args]
  (concat (render-site-files data)
          (site-files args data)
          (render-common-files data "{{sanitized-site}}")))

(defn site-vals
  [ns-name]
  {:ns-name ns-name
   :path (string/replace ns-name "-" "_")
   :docker-name (string/replace ns-name "-" "")
   :dockerised-svr (str (->PascalCase ns-name) "DevSvr")})

(defn dev-profile
  [ns-name args]
  (let [lines (environment-variables args)
        template (string/join "\n             " lines)]
    (render template (site-vals ns-name))))

(defn project-deps
  [args]
  (->> (dependencies args)
       (string/join "\n                ")))

(defn healthcheck-list-template
  []
  (->> ["<ul>"
        "{{#healthchecks}}"
        "  <li>{{name}}: {{status}}</li>"
        "{{/healthchecks}}"
        "</ul>"]
       (string/join \newline)))

(defn page-template
  []
  (->> ["{{>header}}"
        "  <div class=\"default\">"
        "    {{{content}}}"
        "  </div>"
        "{{>footer}}"]
       (string/join \newline)))

(defn person-list
  []
  (->> ["<ul>"
        "{{#people}}"
        "    <li>{{name}},"
        "        {{location}}"
        "        <a href=\"/person/{{id}}/update\">edit</a>"
        "        <a href=\"/person/{{id}}/delete\">delete</a>"
        "    </li>"
        "{{/people}}"
        "</ul>"]
       (string/join \newline)))

(defn add-person
  []
  (->> ["<form action=\"person\" method=\"POST\">"
        "  {{{anti-forgery-field}}}"
        "  <label for=\"name\">Name:</label>"
        "  <input type=\"text\" id=\"name\" name=\"name\"><br>"
        "  <label for=\"location\">Location:</label>"
        "  <input type=\"text\" id=\"location\" name=\"location\">"
        "  <input type=\"submit\" id=\"submit\" value=\"Submit\">"
        "</form>"]
       (string/join \newline)))

(defn update-person
  []
  (->> ["<form method=\"POST\">"
        "  {{{anti-forgery-field}}}"
        "  <input type=\"hidden\" name=\"id\" value=\"{{id}}\">"
        "  <label for=\"name\">Name:</label>"
        "  <input type=\"text\" id=\"name\" name=\"name\" value=\"{{name}}\"><br>"
        "  <label for=\"location\">Location:</label>"
        "  <input type=\"text\" id=\"location\" name=\"location\" value=\"{{location}}\">"
        "  <input type=\"submit\" value=\"Edit\">"
        "</form>"]
       (string/join \newline)))

(defn delete-person
  []
  (->> ["<form method=\"POST\">"
        "  {{{anti-forgery-field}}}"
        "  <input type=\"hidden\" name=\"id\" value=\"{{id}}\">"
        "  Are you sure you want to delete {{name}}, {{location}}?<br>"
        "  <input type=\"submit\" value=\"Yes\">"
        "</form>"]
       (string/join \newline)))

(defn introduction
  []
  (->> ["<h1>Care to add yourself to the list of people...?</h1>"
        ""
        "{{>add-person}}"
        ""
        "{{>person-list}}"]
       (string/join \newline)))

(defn site-var-map
  [ns-name options]
  {:healthcheck-list-template (healthcheck-list-template)
   :page-template (page-template)
   :add-person (add-person)
   :update-person (update-person)
   :delete-person (delete-person)
   :person-list (person-list)
   :introduction (introduction)
   :project-deps (project-deps options)
   :dev-profile (dev-profile ns-name options)})

(defn site-template-data
  [project-name ns-name options]
  (let [sanitized-ns-name (sanitize-ns ns-name)]
    (merge {:name project-name
            :ns-name sanitized-ns-name
            :site-ns-name sanitized-ns-name
            :api-ns-name (sanitize-ns (:api-name options))
            :year (str (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR))
            :project-root (str project-name "/")
            :sanitized-site (name-to-path ns-name)
            :dockerised-svr (str (->PascalCase ns-name) "DevSvr")
            :site-docker-name (string/replace ns-name "-" "")
            :site-dockerised-svr (str (->PascalCase ns-name) "DevSvr")
            :site-path (string/replace ns-name "-" "_")  
            :name-template "{{name}}"
            :location-template "{{location}}"
            :anti-forgery-field "{{{anti-forgery-field}}}"
            :title-template "{{title}}"
            :docker-ip (:docker-ip options)
            :db-name (string/replace project-name "-" "_")}
          (site-var-map sanitized-ns-name options))))
