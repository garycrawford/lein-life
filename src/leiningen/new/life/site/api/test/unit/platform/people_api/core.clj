(ns {{ns-name}}.unit.platform.people-api.core
  (:require [midje.sweet :refer :all]
            [cheshire.core :refer [encode]]
            [clj-http.client :as client]
            [ring.util.response :refer [status header created]]
            [{{ns-name}}.platform.people-api.core :refer :all]))

(def person-uri "http://{{docker-ip}}:4321/api/people/id")

(def person {:name "anon" :location "hidden"})
(def person-with-id {:id "id" :name "anon" :location "hidden"})
(def person-response {:result {:name "anon" :location "hidden"}})
(def person-response-string (encode person-response))
(def person-http-response {:body person-response-string})
(def people-list [person])
(def people-list-response {:result people-list})
(def people-list-response-string (encode people-list-response))
(def people-list-http-response {:body people-list-response-string})

(def response-204 (-> (status {} 204)
                      (header "Content-Type" "application/json")))

(facts "response to the parse-response function"
       (fact "should contain a result structured as edn"
             (parse-response people-list-http-response) => people-list)

       (fact "should return nil when no body is provided"
             (parse-response nil) => nil))

(facts "string response from person-by-id-uri"
       (fact "should return a uri"
             (person-by-id-uri "id") => person-uri))

(facts "response from get-people"
       (fact "should contain a vector of people"
             (get-people) => people-list
             (provided
               (client/get people-uri) => people-list-http-response)))

(facts "response from get-person"
       (fact "should contain a person map"
             (get-person "id") => person
             (provided
               (client/get person-uri) => person-http-response)))

(facts "response from create-person"
       (fact "should contain created success flag"
             (create-person person) => (contains {:created true})
             (provided
               (client/post people-uri {:form-params person}) => (-> (created "http://{{docker-ip}}:4321/api/people/id" (encode {:result {:created true :id "id"}}))
                                                                     (header "Content-Type" "application/json"))))

       (fact "should contain id of created person"
             (create-person person) => (contains {:id "id"})
             (provided
               (client/post people-uri {:form-params person}) => (-> (created "http://{{docker-ip}}:4321/api/people/id" (encode {:result {:created true :id "id"}}))
                                                                     (header "Content-Type" "application/json")))))

(facts "response from update-person"
       (fact "should contain updated success flag"
             (update-person person-with-id) => {:updated true}
             (provided
               (client/put person-uri {:form-params person-with-id}) => (-> response-204
                                                                            (header "Location" "http://{{docker-ip}}:4321/api/people/id")))))

(facts "response from delete-person"
       (fact "should contain deleted success flag"
             (delete-person "id") => {:deleted true}
             (provided
               (client/delete person-uri) => response-204)))
