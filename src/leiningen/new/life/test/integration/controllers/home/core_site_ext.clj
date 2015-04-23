(ns {{ns-name}}.integration.controllers.home.core
  (:require [midje.sweet :refer [fact facts anything =>]]
            [kerodon.core :refer [session visit]]
            [kerodon.test :refer [has status?]]
            [metrics.core :refer [new-registry]]
            [{{ns-name}}.components.jetty.lifecycle :refer [create-handler]]
            [{{ns-name}}.components.mongodb.core :refer [find-one-by-query]]))

(def app (partial create-handler (new-registry)))

(facts "for each call to index"
  (fact "the response has a 200 status code"
    (-> (app {:home {:mongodb {:db ..db..}}})
        session
        (visit "/")
        (has (status? 200))) => anything
    (provided
        (find-one-by-query {:db ..db..} "people" {}) => {:name     "Anonomous User"
                                                         :location "Timbuktu"})))
