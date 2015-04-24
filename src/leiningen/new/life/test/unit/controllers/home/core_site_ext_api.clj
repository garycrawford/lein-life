(ns {{ns-name}}.unit.controllers.home.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.controllers.home.core :refer :all]
            [clj-http.client :as client]
            [cheshire.core :refer [encode]]))

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

(def api-list-people-response {:body (encode {:result [response]})})
(def api-list-no-people-response {:body (encode {:result []})})

(facts "for each call to index"
    (fact "the response has a 200 status code"
      (index-get) => (status? 200)
      (provided
        (client/get "http://192.168.59.103:4321/api/people") => api-list-people-response))
  
    (fact "the response has a text/html content type"
      (index-get) => (content-type? "text/html")
      (provided
        (client/get "http://192.168.59.103:4321/api/people") => api-list-people-response))
  
    (fact "the response model is well formed"
      (let [response (index-get)]
        (get-in response [:body :model])) => response 
      (provided
        (client/get "http://192.168.59.103:4321/api/people") => api-list-people-response))
  
    (fact "the correct view is returned for a return visitor"
      (let [response (index-get)]
        (get-in response [:body :view :path])) => "templates/home/welcome.mustache"
      (provided
        (client/get "http://192.168.59.103:4321/api/people") => api-list-people-response))
  
    (fact "the correct view is returned for a first time visitor"
      (let [response (index-get)]
        (get-in response [:body :view :path])) => "templates/home/introduction.mustache"
      (provided
        (client/get "http://192.168.59.103:4321/api/people") => api-list-no-people-response))

    (fact "a view function is returned"
      (let [response (index-get)]
        (get-in response [:body :view :fn])) => fn?
      (provided
        (client/get "http://192.168.59.103:4321/api/people") => api-list-people-response)))
