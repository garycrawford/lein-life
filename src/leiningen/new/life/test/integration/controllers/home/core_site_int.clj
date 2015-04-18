(ns {{ns-name}}.integration.controllers.home.core
  (:require [midje.sweet :refer [fact facts anything =>]]
            [kerodon.core :refer [session visit]]
            [kerodon.test :refer [has status?]]
            [metrics.core :refer [new-registry]]
            [{{ns-name}}.models.home :refer [about-model]]
            [{{ns-name}}.components.jetty.lifecycle :refer [create-handler]]))

(def app (create-handler (new-registry)))

(facts "for each call to index"
  (fact "the response has a 200 status code"
    (-> app
        session
        (visit "/")
        (has (status? 200))) => anything
    (provided
        (about-model) => {:name     "Anonomous User"
                          :location "Timbuktu"})))
