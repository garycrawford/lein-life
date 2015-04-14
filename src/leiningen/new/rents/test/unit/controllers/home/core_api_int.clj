(ns {{ns-name}}.unit.controllers.home.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.home.core :refer :all]
            [{{ns-name}}.models.home :refer [about-model]]))

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
  (fact "successful response has a 200 status code"
    (index-get) => (status? 200)
    (provided
      (about-model) => response))
  
  (fact "the response has an application/json content type"
    (index-get) => (content-type? "application/json")
    (provided
      (about-model) => response))
  
  (fact "the response model is well formed"
    (let [response (index-get)]
      (:body response)) => response 
    (provided
      (about-model) => response)))
