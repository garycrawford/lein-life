(ns {{ns-name}}.unit.controllers.home.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.home.core :refer :all]
            [{{ns-name}}.platform.people-api.core :refer [get-people]]))

(defn status?
  [expected-status]
  (fn [{actual-status :status}]
    (= actual-status expected-status)))

(defn content-type?
  [expected-content-type]
  (fn [{headers :headers}]
    (let [actual-content-type (get headers "Content-Type")]
      (= actual-content-type expected-content-type))))

(def people [{:name "Anonomous User" :location "Timbuktu"}])

(facts "for each call to index"
    (fact "the response has a 200 status code"
      (home) => (status? 200)
      (provided
        (get-people) => people))
  
    (fact "the response has a text/html content type"
      (home) => (content-type? "text/html")
      (provided
        (get-people) => people))
  
    (fact "the response model is well formed"
      (let [response (home)]
        (get-in response [:body :model])) => (contains {:people people})
      (provided
        (get-people) => people))
  
    (fact "the correct view is returned for a first time visitor"
      (let [response (home)]
        (get-in response [:body :view :path])) => "templates/home/introduction.mustache"
      (provided
        (get-people) => []))

    (fact "a view function is returned"
      (let [response (home)]
        (get-in response [:body :view :fn])) => fn?
      (provided
        (get-people) => people)))
