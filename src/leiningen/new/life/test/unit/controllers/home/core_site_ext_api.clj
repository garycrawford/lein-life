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

(facts "the home function response map"
  (fact "contains a 200 status code"
    (home) => (status? 200)
    (provided
      (get-people) => people))

  (fact "contains a text/html content type"
    (home) => (content-type? "text/html")
    (provided
      (get-people) => people))

  (fact "contains a view model containing people list data for the view"
    (let [response (home)]
      (get-in response [:body :model])) => (contains {:people people})
    (provided
      (get-people) => people))

  (fact "contains a path to the view template to be rendered"
    (let [response (home)]
      (get-in response [:body :view :path])) => "templates/home/introduction.mustache"
    (provided
      (get-people) => []))

  (fact "contains a view function which renders the view with the view model data"
    (let [response (home)]
      (get-in response [:body :view :fn])) => fn?
    (provided
      (get-people) => people)))
