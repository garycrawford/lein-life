(ns {{ns-name}}.platform.people-api.core
  (:require [clj-http.client :as client]
            [taoensso.timbre :refer [info]]
            [cheshire.core :refer [decode]]
            [dire.core :refer [with-handler!]]))

(def people-uri "http://{{docker-ip}}:4321/api/people")

(defn person-by-id-uri
  "Creates a URI to obtain person information from a person id"
  [id]
  (str people-uri "/" id))

(defn parse-response
  "Parses the result of an http response as edn"
  [{:keys [body]}]
  (-> body
      (decode true)
      :result))

(defn get-people
  []
  (-> people-uri
      client/get
      parse-response))

(defn create-person
  [person]
  (-> people-uri
      (client/post {:form-params person})
      parse-response))

(defn get-person
  [id]
  (-> id
      person-by-id-uri
      client/get
      parse-response))

(defn update-person
  [{:keys [id] :as person}]
  (let [status (-> id
                   person-by-id-uri
                   (client/put {:form-params person})
                   :status)]
    (if (= status 204) {:updated true} {:updated false})))

(defn delete-person
  [id]
  (let [status (-> id
                   person-by-id-uri
                   client/delete
                   :status)]
    (if (= status 204) {:deleted true} {:deleted false})))

(with-handler! #'get-person
  [:status 404]
  (fn [e & args] (info "Person with id doesn't exist" args)))
