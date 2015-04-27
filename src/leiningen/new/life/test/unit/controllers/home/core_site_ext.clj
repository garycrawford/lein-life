(ns {{ns-name}}.unit.controllers.home.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.home.core :refer :all]
            [{{ns-name}}.components.mongodb.core :refer [find-by-query]]))

(def collection "people")

(defn status?
  [expected-status]
  (fn [{actual-status :status}]
    (= actual-status expected-status)))

(defn content-type?
  [expected-content-type]
  (fn [{headers :headers}]
    (let [actual-content-type (get headers "Content-Type")]
      (= actual-content-type expected-content-type))))

(def response [{:name     "Anonomous User"
                :location "Timbuktu"}])

(facts "the home function response map"
  (let [mongo-component {:db ..db..}
        home-component {:mongodb mongo-component}]
    (fact "contains a 200 status code"
      (home home-component) => (status? 200)
      (provided
        (find-by-query mongo-component collection {}) => response))
  
    (fact "contains a text/html content type"
      (home home-component) => (content-type? "text/html")
      (provided
        (find-by-query mongo-component collection {}) => response))
  
    (fact "contains a view model containing people list data for the view"
      (let [response (home home-component)]
        (get-in response [:body :model])) => (contains {:people [{:name     "Anonomous User"
                                                                  :location "Timbuktu"}]})
      (provided
        (find-by-query mongo-component collection {}) => response))
  
    (fact "contains a path to the view template to be rendered"
      (let [response (home home-component)]
        (get-in response [:body :view :path])) => "templates/home/introduction.mustache"
      (provided
        (find-by-query mongo-component collection {}) => nil))

    (fact "contains a view function which renders the view with the view model data"
      (let [response (home home-component)]
        (get-in response [:body :view :fn])) => fn?
      (provided
        (find-by-query mongo-component collection {}) => response))))
