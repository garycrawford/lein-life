(ns {{ns-name}}.unit.controllers.home.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.home.core :refer :all]
            [{{ns-name}}.components.mongodb.core :refer [find-by-query find-by-id update delete insert]]
            [ring.util.response :refer [get-header]]))

(def collection "people")

(defn location?
  [expected-location]
  (fn [response]
    (let [actual-location (get-header response "Location")]
      (= actual-location expected-location))))

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
                                 person-with-id (merge person {:id ..id..})
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

       (fact "contains a view model populated with people"
             (let [response (home home-component)]
               (get-in response [:body :model])) => (contains {:people people})
               (provided
                 (find-by-query mongo-component collection {}) => people))

       (fact "contains a path to the view template to be rendered"
            (let [response (home home-component)]
              (get-in response [:body :view :path])) => "templates/home/introduction.mustache"
            (provided
              (find-by-query mongo-component collection {}) => people))
   
       (fact "contains a view function"
            (let [response (home home-component)]
              (get-in response [:body :view :fn])) => fn?
            (provided
              (find-by-query mongo-component collection {}) => people)))

(facts "the update-person-get function response map"
       (facts "for users which exist"
              (fact "contains a 200 status code"
                    (update-person-get home-component {:id ..id..}) => (status? 200)
                    (provided
                      (find-by-id mongo-component collection ..id..) => person))
 
              (fact "contains a text/html content type"
                    (update-person-get home-component {:id ..id..}) => (content-type? "text/html")
                    (provided
                      (find-by-id mongo-component collection ..id..) => people))

              (fact "contains a view model with person data"
                    (let [response (update-person-get home-component {:id ..id..})]
                      (get-in response [:body :model])) => (contains person)
                    (provided
                      (find-by-id mongo-component collection ..id..) => person))

              (fact "contains a path to the view template to be rendered"
                    (let [response (update-person-get home-component {:id ..id..})]
                      (get-in response [:body :view :path])) => "templates/home/update-person.mustache"
                    (provided
                      (find-by-id mongo-component collection ..id..) => person))

              (fact "contains a view function"
                    (let [response (update-person-get home-component {:id ..id..})]
                      (get-in response [:body :view :fn])) => fn?
                    (provided
                      (find-by-id mongo-component collection ..id..) => person)))

       (facts "for users which don't exist"
              (fact "contains a 404 status code"
                    (update-person-get home-component {:id ..id..}) => (status? 404)
                    (provided
                      (find-by-id mongo-component collection ..id..) => nil))

              (fact "contains a text/html content type"
                    (update-person-get home-component {:id ..id..}) => (content-type? "text/html")
                    (provided
                      (find-by-id mongo-component collection ..id..) => nil))

              (fact "contains an empty view model"
                    (let [response (update-person-get home-component {:id ..id..})]
                      (get-in response [:body :model])) => {}
                    (provided
                      (find-by-id mongo-component collection ..id..) => nil))

              (fact "contains a path to the not-found template"
                    (let [response (update-person-get home-component {:id ..id..})]
                      (get-in response [:body :view :path])) => "templates/home/not-found.mustache"
                    (provided
                      (find-by-id mongo-component collection ..id..) => nil))))

(facts "the delete-person-get function response map"
       (facts "for users which exist"
              (fact "contains a 200 status code"
                    (delete-person-get home-component {:id ..id..}) => (status? 200)
                    (provided
                      (find-by-id mongo-component collection ..id..) => person))
 
              (fact "contains a text/html content type"
                    (delete-person-get home-component {:id ..id..}) => (content-type? "text/html")
                    (provided
                      (find-by-id mongo-component collection ..id..) => people))

              (fact "contains a view model with person data"
                    (let [response (delete-person-get home-component {:id ..id..})]
                      (get-in response [:body :model])) => (contains person)
                    (provided
                      (find-by-id mongo-component collection ..id..) => person))

              (fact "contains a path to the view template to be rendered"
                    (let [response (delete-person-get home-component {:id ..id..})]
                      (get-in response [:body :view :path])) => "templates/home/delete-person.mustache"
                    (provided
                      (find-by-id mongo-component collection ..id..) => person))

              (fact "contains a view function"
                    (let [response (delete-person-get home-component {:id ..id..})]
                      (get-in response [:body :view :fn])) => fn?
                    (provided
                      (find-by-id mongo-component collection ..id..) => person)))

       (facts "for users which don't exist"
              (fact "contains a 404 status code"
                    (delete-person-get home-component {:id ..id..}) => (status? 404)
                    (provided
                      (find-by-id mongo-component collection ..id..) => nil))

              (fact "contains a text/html content type"
                    (delete-person-get home-component {:id ..id..}) => (content-type? "text/html")
                    (provided
                      (find-by-id mongo-component collection ..id..) => nil))

              (fact "contains an empty view model"
                    (let [response (delete-person-get home-component {:id ..id..})]
                      (get-in response [:body :model])) => {}
                    (provided
                      (find-by-id mongo-component collection ..id..) => nil))

              (fact "contains a path to the not-found template"
                    (let [response (delete-person-get home-component {:id ..id..})]
                      (get-in response [:body :view :path])) => "templates/home/not-found.mustache"
                    (provided
                      (find-by-id mongo-component collection ..id..) => nil))))

(facts "the update-person-post function response map"
       (facts "for users which exist"
              (fact "contains a 303 status code"
                    (update-person-post home-component person-with-id) => (status? 303)
                    (provided
                      (update mongo-component collection person-with-id) => {:count 1}))
 
              (fact "contains a '/' location header"
                    (update-person-post home-component person-with-id) => (location? "/")
                    (provided
                      (update mongo-component collection person-with-id) => {:count 1})))

       (facts "for users which don't exist"
              (fact "contains a 404 status code"
                    (update-person-post home-component person-with-id) => (status? 404)
                    (provided
                      (update mongo-component collection person-with-id) => {:count 0}))

              (fact "contains a text/html content"
                    (update-person-post home-component person-with-id) => (content-type? "text/html")
                    (provided
                      (update mongo-component collection person-with-id) => {:count 0}))

              (fact "contains an empty view model"
                    (let [response (update-person-post home-component person-with-id)]
                      (get-in response [:body :model])) => {}
                    (provided
                      (update mongo-component collection person-with-id) => {:count 0}))

              (fact "contains a path to the not-found template"
                    (let [response (update-person-post home-component person-with-id)]
                      (get-in response [:body :view :path])) => "templates/home/not-found.mustache"
                    (provided
                      (update mongo-component collection person-with-id) => {:count 0}))))

(facts "the delete-person-post function response map"
       (facts "for users which exist"
              (fact "contains a 303 status code"
                    (delete-person-post home-component {:id ..id..}) => (status? 303)
                    (provided
                      (delete mongo-component collection ..id..) => {:count 1}))
 
              (fact "contains a '/' location header"
                    (delete-person-post home-component {:id ..id..}) => (location? "/")
                    (provided
                      (delete mongo-component collection ..id..) => {:count 1})))

       (facts "for users which don't exist"
              (fact "contains a 404 status code"
                    (delete-person-post home-component {:id ..id..}) => (status? 404)
                    (provided
                      (delete mongo-component collection ..id..) => {:count 0}))

              (fact "contains a text/html content"
                    (delete-person-post home-component {:id ..id..}) => (content-type? "text/html")
                    (provided
                      (delete mongo-component collection ..id..) => {:count 0}))

              (fact "contains an empty view model"
                    (let [response (delete-person-post home-component {:id ..id..})]
                      (get-in response [:body :model])) => {}
                    (provided
                      (delete mongo-component collection ..id..) => {:count 0}))

              (fact "contains a path to the not-found template"
                    (let [response (delete-person-post home-component {:id ..id..})]
                      (get-in response [:body :view :path])) => "templates/home/not-found.mustache"
                    (provided
                      (delete mongo-component collection ..id..) => {:count 0}))))

(facts "the create-person-post function response map"
       (fact "contains a 303 status code"
             (create-person-post home-component person) => (status? 303)
             (provided
               (insert mongo-component collection person) => anything))
 
       (fact "contains a '/' location header"
             (create-person-post home-component person) => (location? "/")
             (provided
               (insert mongo-component collection person) => anything)))
