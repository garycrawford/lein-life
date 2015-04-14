(ns {{ns-name}}.unit.controllers.home.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.home.core :refer :all]
            [{{ns-name}}.components.mongodb.core :refer [find-one-by-query]]))

(defn status?
  [expected-status]
  (fn [{actual-status :status}]
    (= actual-status expected-status)))

(defn content-type?
  [expected-content-type]
  (fn [{headers :headers}]
    (let [actual-content-type (get headers "Content-Type")]
      (= actual-content-type expected-content-type))))

(def response {:name     "Anonomous User"
               :location "Timbuktu"})

(facts "for each call to index"
  (let [mongo-component {:db ..db..}
        home-component {:mongodb mongo-component}]

    (fact "successful response has a 200 status code"
      (index-get home-component) => (status? 200)
      (provided
        (find-one-by-query mongo-component "visitors" {}) => response))
  
    (fact "the response has an application/json content type"
      (index-get home-component) => (content-type? "application/json")
      (provided
        (find-one-by-query mongo-component "visitors" {}) => response))
  
    (fact "the response model is well formed"
      (let [response (index-get home-component)]
        (:body response)) => response 
      (provided
        (find-one-by-query mongo-component "visitors" {}) => response))))
