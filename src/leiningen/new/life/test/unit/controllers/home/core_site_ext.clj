(ns {{ns-name}}.unit.controllers.home.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.home.core :refer :all]
            [{{ns-name}}.components.mongodb.core :refer [find-by-query find-by-id]]))

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

(background (around :facts (let [person {:name "Anonomous User" :location "Timbuktu"}
                                 people [person]
                                 mongo-component {:db ..db..}
                                 home-component {:mongodb mongo-component}] ?form)))

(facts "the home function response map"

       (fact "contains a 200 status code"
             (home home-component) => (status? 200)
             (provided
               (find-by-query mongo-component collection {}) => people))

       (fact "contains a text/html content type"
             (home home-component) => (content-type? "text/html")
             (provided
               (find-by-query mongo-component collection {}) => people))

       (fact "contains a view model containing people list data for the view"
             (let [response (home home-component)]
               (get-in response [:body :model])) => (contains {:people people})
               (provided
                 (find-by-query mongo-component collection {}) => people))

       (fact "contains a path to the view template to be rendered"
            (let [response (home home-component)]
              (get-in response [:body :view :path])) => "templates/home/introduction.mustache"
            (provided
              (find-by-query mongo-component collection {}) => people))
   
       (fact "contains a view function which renders the view with the view model data"
            (let [response (home home-component)]
              (get-in response [:body :view :fn])) => fn?
            (provided
              (find-by-query mongo-component collection {}) => people)))

(facts "the update-person-get function response map"
       (facts "for users which exist"
              (fact "contains a 200 status code when the person exists"
                    (update-person-get home-component {:id ..id..}) => (status? 200)
                    (provided
                      (find-by-id mongo-component collection ..id..) => person))
 
              (fact "contains a text/html content type when 200"
                    (update-person-get home-component {:id ..id..}) => (content-type? "text/html")
                    (provided
                      (find-by-id mongo-component collection ..id..) => people))

              (fact "contains a view model with person data when person exists"
                    (let [response (update-person-get home-component {:id ..id..})]
                      (get-in response [:body :model])) => (contains person)
                    (provided
                      (find-by-id mongo-component collection ..id..) => person))

              (fact "contains a path to the view template to be rendered"
                    (let [response (update-person-get home-component {:id ..id..})]
                      (get-in response [:body :view :path])) => "templates/home/update-person.mustache"
                    (provided
                      (find-by-id mongo-component collection ..id..) => person))

              (fact "contains a view function which renders the view with the view model data"
                    (let [response (update-person-get home-component {:id ..id..})]
                      (get-in response [:body :view :fn])) => fn?
                    (provided
                      (find-by-id mongo-component collection ..id..) => person)))

       (facts "for users which don't exist"
              (fact "contains a 404 status code when the person does not exist"
                    (update-person-get home-component {:id ..id..}) => (status? 404)
                    (provided
                      (find-by-id mongo-component collection ..id..) => nil))

              (fact "contains a text/html content type when 404"
                    (update-person-get home-component {:id ..id..}) => (content-type? "text/html")
                    (provided
                      (find-by-id mongo-component collection ..id..) => nil))

              (fact "contains an empty view model when person does not exist"
                    (let [response (update-person-get home-component {:id ..id..})]
                      (get-in response [:body :model])) => {}
                    (provided
                      (find-by-id mongo-component collection ..id..) => nil))

              (fact "contains a path to the not-found template when no person exists"
                    (let [response (update-person-get home-component {:id ..id..})]
                      (get-in response [:body :view :path])) => "templates/home/not-found.mustache"
                    (provided
                      (find-by-id mongo-component collection ..id..) => nil))))
