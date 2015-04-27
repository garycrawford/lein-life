(ns {{ns-name}}.unit.controllers.home.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.home.core :refer :all]
            [{{ns-name}}.platform.people-api.core :refer [get-person get-people update-person delete-person create-person]]
            [ring.util.response :refer [get-header]]))

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
                                 people [person]] ?form)))

(facts "the home function response map"
       (fact "contains a 200 status code"
             (home) => (status? 200)
             (provided
               (get-people) => people))

       (fact "contains a text/html content type"
             (home) => (content-type? "text/html")
             (provided
               (get-people) => people))

       (fact "contains a view model populated with people"
             (let [response (home)]
               (get-in response [:body :model])) => (contains {:people people})
               (provided
                 (get-people) => people))

       (fact "contains a path to the view template to be rendered"
            (let [response (home)]
              (get-in response [:body :view :path])) => "templates/home/introduction.mustache"
            (provided
              (get-people) => people))
   
       (fact "contains a view function"
            (let [response (home)]
              (get-in response [:body :view :fn])) => fn?
            (provided
              (get-people) => people)))

(facts "the update-person-get function response map"
       (facts "for users which exist"
              (fact "contains a 200 status code"
                    (update-person-get {:id ..id..}) => (status? 200)
                    (provided
                      (get-person ..id..) => person))
 
              (fact "contains a text/html content type"
                    (update-person-get {:id ..id..}) => (content-type? "text/html")
                    (provided
                      (get-person ..id..) => people))

              (fact "contains a view model with person data"
                    (let [response (update-person-get {:id ..id..})]
                      (get-in response [:body :model])) => (contains person)
                    (provided
                      (get-person ..id..) => person))

              (fact "contains a path to the view template to be rendered"
                    (let [response (update-person-get {:id ..id..})]
                      (get-in response [:body :view :path])) => "templates/home/update-person.mustache"
                    (provided
                      (get-person ..id..) => person))

              (fact "contains a view function"
                    (let [response (update-person-get {:id ..id..})]
                      (get-in response [:body :view :fn])) => fn?
                    (provided
                      (get-person ..id..) => person)))

       (facts "for users which don't exist"
              (fact "contains a 404 status code"
                    (update-person-get {:id ..id..}) => (status? 404)
                    (provided
                      (get-person ..id..) => nil))

              (fact "contains a text/html content type"
                    (update-person-get {:id ..id..}) => (content-type? "text/html")
                    (provided
                      (get-person ..id..) => nil))

              (fact "contains an empty view model"
                    (let [response (update-person-get {:id ..id..})]
                      (get-in response [:body :model])) => {}
                    (provided
                      (get-person ..id..) => nil))

              (fact "contains a path to the not-found template"
                    (let [response (update-person-get {:id ..id..})]
                      (get-in response [:body :view :path])) => "templates/home/not-found.mustache"
                    (provided
                      (get-person ..id..) => nil))))

(facts "the delete-person-get function response map"
       (facts "for users which exist"
              (fact "contains a 200 status code"
                    (delete-person-get {:id ..id..}) => (status? 200)
                    (provided
                      (get-person ..id..) => person))
 
              (fact "contains a text/html content type"
                    (delete-person-get {:id ..id..}) => (content-type? "text/html")
                    (provided
                      (get-person ..id..) => people))

              (fact "contains a view model with person data"
                    (let [response (delete-person-get {:id ..id..})]
                      (get-in response [:body :model])) => (contains person)
                    (provided
                      (get-person ..id..) => person))

              (fact "contains a path to the view template to be rendered"
                    (let [response (delete-person-get {:id ..id..})]
                      (get-in response [:body :view :path])) => "templates/home/delete-person.mustache"
                    (provided
                      (get-person ..id..) => person))

              (fact "contains a view function"
                    (let [response (delete-person-get {:id ..id..})]
                      (get-in response [:body :view :fn])) => fn?
                    (provided
                      (get-person ..id..) => person)))

       (facts "for users which don't exist"
              (fact "contains a 404 status code"
                    (delete-person-get {:id ..id..}) => (status? 404)
                    (provided
                      (get-person ..id..) => nil))

              (fact "contains a text/html content type"
                    (delete-person-get {:id ..id..}) => (content-type? "text/html")
                    (provided
                      (get-person ..id..) => nil))

              (fact "contains an empty view model"
                    (let [response (delete-person-get {:id ..id..})]
                      (get-in response [:body :model])) => {}
                    (provided
                      (get-person ..id..) => nil))

              (fact "contains a path to the not-found template"
                    (let [response (delete-person-get {:id ..id..})]
                      (get-in response [:body :view :path])) => "templates/home/not-found.mustache"
                    (provided
                      (get-person ..id..) => nil))))

(facts "the update-person-post function response map"
       (facts "for users which exist"
              (fact "contains a 303 status code"
                    (update-person-post person-with-id) => (status? 303)
                    (provided
                      (update-person person-with-id) => {:count 1}))
 
              (fact "contains a '/' location header"
                    (update-person-post person-with-id) => (location? "/")
                    (provided
                      (update-person person-with-id) => {:count 1})))

       (facts "for users which don't exist"
              (fact "contains a 404 status code"
                    (update-person-post person-with-id) => (status? 404)
                    (provided
                      (update-person person-with-id) => {:count 0}))

              (fact "contains a text/html content"
                    (update-person-post person-with-id) => (content-type? "text/html")
                    (provided
                      (update-person person-with-id) => {:count 0}))

              (fact "contains an empty view model"
                    (let [response (update-person-post person-with-id)]
                      (get-in response [:body :model])) => {}
                    (provided
                      (update-person person-with-id) => {:count 0}))

              (fact "contains a path to the not-found template"
                    (let [response (update-person-post person-with-id)]
                      (get-in response [:body :view :path])) => "templates/home/not-found.mustache"
                    (provided
                      (update-person person-with-id) => {:count 0}))))

(facts "the delete-person-post function response map"
       (facts "for users which exist"
              (fact "contains a 303 status code"
                    (delete-person-post {:id ..id..}) => (status? 303)
                    (provided
                      (delete-person ..id..) => {:count 1}))
 
              (fact "contains a '/' location header"
                    (delete-person-post {:id ..id..}) => (location? "/")
                    (provided
                      (delete-person ..id..) => {:count 1})))

       (facts "for users which don't exist"
              (fact "contains a 404 status code"
                    (delete-person-post {:id ..id..}) => (status? 404)
                    (provided
                      (delete-person ..id..) => {:count 0}))

              (fact "contains a text/html content"
                    (delete-person-post {:id ..id..}) => (content-type? "text/html")
                    (provided
                      (delete-person ..id..) => {:count 0}))

              (fact "contains an empty view model"
                    (let [response (delete-person-post {:id ..id..})]
                      (get-in response [:body :model])) => {}
                    (provided
                      (delete-person ..id..) => {:count 0}))

              (fact "contains a path to the not-found template"
                    (let [response (delete-person-post {:id ..id..})]
                      (get-in response [:body :view :path])) => "templates/home/not-found.mustache"
                    (provided
                      (delete-person ..id..) => {:count 0}))))

(facts "the create-person-post function response map"
       (fact "contains a 303 status code"
             (create-person-post person) => (status? 303)
             (provided
               (create-person person) => anything))
 
       (fact "contains a '/' location header"
             (create-person-post person) => (location? "/")
             (provided
               (create-person person) => anything)))
