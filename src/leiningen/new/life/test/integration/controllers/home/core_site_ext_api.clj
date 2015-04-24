(ns {{ns-name}}.integration.controllers.home.core
  (:require [midje.sweet :refer [fact facts anything =>]]
            [kerodon.core :refer [session visit]]
            [kerodon.test :refer [has status?]]
            [metrics.core :refer [new-registry]]
            [clj-http.client :as client]
            [cheshire.core :refer [encode]]
            [{{ns-name}}.components.jetty.lifecycle :refer [create-handler]]))

(def app (partial create-handler (new-registry)))

(def response {:name     "Anonomous User"
               :location "Timbuktu"})

(def api-list-people-response {:body (encode {:result [response]})})

(facts "for each call to index"
  (fact "the response has a 200 status code"
    (-> (app {:home {:mongodb {:db ..db..}}})
        session
        (visit "/")
        (has (status? 200))) => anything
    (provided
        (client/get "http://192.168.59.103:4321/api/people") => api-list-people-response)))
