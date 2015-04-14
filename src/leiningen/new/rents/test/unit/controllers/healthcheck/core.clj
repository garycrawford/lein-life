(ns {{ns-name}}.unit.controllers.healthcheck.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.healthcheck.core :refer :all]))

(defn status?
  [expected-status]
  (fn [{actual-status :status}]
    (= actual-status expected-status)))

(defn content-type?
  [expected-content-type]
  (fn [{headers :headers}]
    (let [actual-content-type (get headers "Content-Type")]
      (= actual-content-type expected-content-type))))

(facts "for each call to index"
  (fact "the response has a 200 status code"
    (index) => (status? 200))

  (fact "the response has a text/html content type"
    (index) => (content-type? "text/html"))

  (fact "the response model is well formed"
    (let [response (index)]
      (get-in response [:body :model])) =>
        {:healthchecks [{:name "service 1" :status "STARTING"}
                        {:name "service 2" :status "STARTED"}
                        {:name "service 3" :status "STOPPING"}
                        {:name "service 4" :status "STOPPED"}
                        {:name "service 5" :status "ERRORED"}]})

  (fact "the correct view is returned"
    (let [response (index)]
      (get-in response [:body :view :path])) =>
        "templates/healthcheck/healthcheck-list.mustache")

  (fact "a view function is returned"
    (let [response (index)]
      (get-in response [:body :view :fn])) => fn?))
