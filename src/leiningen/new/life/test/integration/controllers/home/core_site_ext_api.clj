(ns {{ns-name}}.integration.controllers.home.core
  (:require [midje.sweet :refer [fact facts anything => against-background]]
            [com.stuartsierra.component :as component]
            [kerodon.core :refer :all]
            [kerodon.test :refer :all]
            [{{ns-name}}.components.jetty.lifecycle :as site]
            [clj-http.client :as http]
            [cheshire.core :refer [encode]]
            [clojure.string :refer [split]]))

(def people-uri "http://192.168.59.103:4321/api/people")

(def db (atom []))

(defn get-person-response
  [uri]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (encode {:result @db})})

(defn get-people-response
  [uri]
  (let [id (last (split uri #"/"))
        person (first (filter #(= (:id %) id) @db))]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (encode {:result person})}))

(defn get-response
  [uri]
  (if (= uri people-uri)
    (get-person-response uri)
    (get-people-response uri)))

(defn create-person-response
  [uri {:keys [form-params]}]
  (swap! db conj (assoc form-params :id "123"))
  {:status 201
   :headers {"Content-Type" "application/json"
             "Location" (str people-uri "/" 123)}})

(defn remove-by-id
  [id]
  (fn [entries]
    (filter #(not= (:id %) id) entries)))

(defn update-person-response
  [uri {:keys [form-params]}]
  (let [id (last (split uri #"/"))
        {:keys [name location]} form-params
        person (first (filter #(= (:id %) id) @db))
        updated-person (merge person {:name name :location location})]
    (swap! db (remove-by-id id))
    (swap! db conj updated-person)
    {:status 204
     :headers {"Content-Type" "application/json"
               "Location" (str people-uri "/" id)}}))

(defn delete-person-response
  [uri]
  (let [id (last (split uri #"/"))]
    (swap! db (remove-by-id id))
    {:status 204
     :headers {"Content-Type" "application/json"}}))

(against-background [(before :contents (reset! db []))]
                    (fact "People can be added"
                         (with-redefs [http/get get-response
                                       http/post create-person-response]
                           (-> (session (site/create-handler {}))
                               (visit "/")
                               (fill-in "Name:"     "username")
                               (fill-in "Location:" "location")
                               (press "Submit")
                               (follow-redirect)
                               (within [:ul :li]
                                 (has (some-text? "username, location"))))) => anything)

                    (fact "People can be edited"
                         (with-redefs [http/get get-response
                                       http/put update-person-response]
                           (-> (session (site/create-handler {}))
                               (visit "/")
                               (within [:ul :li]
                                 (has (some-text? "username, location")))
                               (follow "edit")
                               (fill-in "Name:"     "edited-username")
                               (fill-in "Location:" "edited-location")
                               (press "Edit")
                               (follow-redirect)
                               (within [:ul :li]
                                 (has (some-text? "edited-username, edited-location"))))) => anything)

                    (fact "People can be deleted"
                         (with-redefs [http/get get-response
                                       http/delete delete-person-response]
                           (-> (session (site/create-handler {}))
                               (visit "/")
                               (within [:ul :li]
                                 (has (some-text? "edited-username, edited-location")))
                               (follow "delete")
                               (press "Yes")
                               (follow-redirect)
                               (has (missing? [:ul :li])))) => anything))
