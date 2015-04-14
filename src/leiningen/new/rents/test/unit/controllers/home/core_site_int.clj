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

(def response {:name    "Anonomous User"
               :location "Timbuktu"})

(facts "for each call to index"
    (fact "the response has a 200 status code"
      (index-get) => (status? 200)
      (provided
        (about-model) => response))
  
    (fact "the response has a text/html content type"
      (index-get) => (content-type? "text/html")
      (provided
        (about-model) => response))
  
    (fact "the response model is well formed"
      (let [response (index-get)]
        (get-in response [:body :model])) => response 
      (provided
        (about-model) => response))
  
    (fact "the correct view is returned for a return visitor"
      (let [response (index-get)]
        (get-in response [:body :view :path])) => "templates/home/welcome.mustache"
      (provided
        (about-model) => response))
  
    (fact "the correct view is returned for a first time visitor"
      (let [response (index-get)]
        (get-in response [:body :view :path])) => "templates/home/introduction.mustache"
      (provided
        (about-model) => nil))

    (fact "a view function is returned"
      (let [response (index-get)]
        (get-in response [:body :view :fn])) => fn?
      (provided
        (about-model) => response)))
