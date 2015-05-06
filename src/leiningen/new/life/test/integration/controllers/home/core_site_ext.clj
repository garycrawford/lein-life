(ns {{ns-name}}.integration.controllers.home.core
  (:require [midje.sweet :refer [fact facts anything => against-background]]
            [com.stuartsierra.component :as component]
            [kerodon.core :refer :all]
            [kerodon.test :refer :all]
            [{{ns-name}}.components.jetty.lifecycle :refer [create-handler]]
            [{{ns-name}}.components.mongodb.lifecycle :refer [new-mongodb]]
            [monger.db :refer [drop-db]]))

(def mongodb (new-mongodb "home_integration_tests"))

(defn setup
  []
  (alter-var-root #'mongodb component/start)
  (drop-db (:db mongodb)))

(defn teardown
  []
  (drop-db (:db mongodb))
  (alter-var-root #'mongodb component/stop))

(against-background [(before :contents (setup)) (after :contents (teardown))]
                    (fact "People can be added"
                         (-> (session (create-handler {:home {:mongodb mongodb}}))
                             (visit "/")
                             (fill-in "Name:"     "username")
                             (fill-in "Location:" "location")
                             (press "Submit")
                             (follow-redirect)
                             (within [:ul :li]
                               (has (some-text? "username, location")))) => anything)

                    (fact "People can be edited"
                         (-> (session (create-handler {:home {:mongodb mongodb}}))
                             (visit "/")
                             (within [:ul :li]
                               (has (some-text? "username, location")))
                             (follow "edit")
                             (fill-in "Name:"     "edited-username")
                             (fill-in "Location:" "edited-location")
                             (press "Edit")
                             (follow-redirect)
                             (within [:ul :li]
                               (has (some-text? "edited-username, edited-location")))) => anything)
                    
                    (fact "People can be deleted"
                         (-> (session (create-handler {:home {:mongodb mongodb}}))
                             (visit "/")
                             (within [:ul :li]
                               (has (some-text? "edited-username, edited-location")))
                             (follow "delete")
                             (press "Yes")
                             (follow-redirect)
                             (has (missing? [:ul :li]))) => anything))
