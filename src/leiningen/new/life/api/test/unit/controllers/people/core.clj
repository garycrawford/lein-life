(ns {{ns-name}}.unit.controllers.people.core
  (:require [midje.sweet :refer :all]
            [ring.util.response :refer [get-header]]
            [ring.util.mime-type :refer [default-mime-types]]
            [{{ns-name}}.checkers.core :refer [location? status? result? content-type?]]
            [{{ns-name}}.controllers.people.core :refer :all]
            [{{ns-name}}.components.mongodb.core :refer [find-by-query find-by-id insert update delete]]))

(def person {:name "name" :location "location"})
(def person-with-id (merge {:id "id"} person))

(facts "when listing people"
  (fact "a result can be an empty array"
    (list-people {:mongodb ..mongodb..}) => (result? [])
    (provided
      (find-by-query ..mongodb.. "people" {}) => []))

  (fact "a result can be a non-empty array"
    (list-people {:mongodb ..mongodb..}) => (result? [person-with-id])
    (provided
      (find-by-query ..mongodb.. "people" {}) => [person-with-id]))

  (fact "only id, name and location fields are returned"
    (list-people {:mongodb ..mongodb..}) => (result? [person-with-id])
    (provided
      (find-by-query ..mongodb.. "people" {}) => [(merge person-with-id {:something :else})])))

(facts "when creating a person"
  (fact "success result will have a 201 status code"
    (create-person {:mongodb ..mongodb..} person) => (status? 201)
    (provided
      (insert ..mongodb.. "people" person) => {:id "id"}))

  (fact "success result will have a populated location header"
    (create-person {:mongodb ..mongodb..} person) => (location? (person-uri "id"))
    (provided
      (insert ..mongodb.. "people" person) => {:id "id"}))

  (fact "success result will have an application/json content type header"
    (create-person {:mongodb ..mongodb..} person) => (content-type? "json")
    (provided
      (insert ..mongodb.. "people" person) => {:id "id"})))

(facts "when reading a person"
  (fact "success will result in a 200 status code"
    (read-person {:mongodb ..mongodb..} ..id..) => (status? 200)
    (provided
      (find-by-id ..mongodb.. "people" ..id..) => person-with-id))

  (fact "success will result in correct data"
    (read-person {:mongodb ..mongodb..} ..id..) => (result? person-with-id)
    (provided
      (find-by-id ..mongodb.. "people" ..id..) => person-with-id)))

(facts "when updating a person"
  (fact "success will result in a 204 status code"
    (update-person {:mongodb ..mongodb..} person-with-id) => (status? 204)
    (provided
      (update ..mongodb.. "people" person-with-id) => irrelevant))

  (fact "success will result will contain a location header"
    (update-person {:mongodb ..mongodb..} person-with-id) => (location? (person-uri "id"))
    (provided
      (update ..mongodb.. "people" person-with-id) => irrelevant))

  (fact "success will have an application/json content type"
    (update-person {:mongodb ..mongodb..} person-with-id) => (content-type? "json")
    (provided
      (update ..mongodb.. "people" person-with-id) => irrelevant)))

(facts "when deleting a person"
  (fact "success will result in a 204 status code"
    (delete-person {:mongodb ..mongodb..} ..id..) => (status? 204)
    (provided
      (delete ..mongodb.. "people" ..id..) => irrelevant))

  (fact "success will have an application/json content type"
    (delete-person {:mongodb ..mongodb..} ..id..) => (content-type? "json")
    (provided
      (delete ..mongodb.. "people" ..id..) => irrelevant)))
