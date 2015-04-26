(ns {{ns-name}}.platform.people-api.core
  (:require [clj-http.client :as client]
            [cheshire.core :refer [decode]]))

(defn parse-people-list-response
  "Extracts the result of an http response containing a people list and converts into a clojure data structure"
  [{:keys [body]}]
  (get (decode body true) :result))

(defn person-by-id-uri
  "Creates a URI to obtain person information from a person id"
  [id]
  (str "http://192.168.59.103:4321/api/people/" id))

(defn parse-person-response
  "Extracts the result of an http response containing a single person and converts into a clojure data structure"
  [{:keys [body]}]
  (get (decode body true) :result))

(defn get-people
  []
  (-> "http://192.168.59.103:4321/api/people"
      client/get
      parse-people-list-response))

(defn create-person
  [person]
  (client/post "http://192.168.59.103:4321/api/people" {:form-params person}))

(defn get-person
  [id]
  (-> id
      person-by-id-uri
      client/get
      parse-person-response))

(defn update-person
  [{:keys [id] :as person}]
  (let [uri (person-by-id-uri id)]
    (client/put uri {:form-params person})))

(defn delete-person
  [id]
  (let [uri (person-by-id-uri id)]
    (client/delete uri {:form-params {:id id}})))
