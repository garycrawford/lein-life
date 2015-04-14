(ns {{ns-name}}.integration.controllers.people.core
  (:require [midje.sweet :refer [fact facts anything => against-background before after contains]]
            [com.stuartsierra.component :as component]
            [metrics.core :refer [new-registry]]
            [ring.mock.request :as mock]
            [example-api.components.jetty.lifecycle :refer [create-handler]]
            [example-api.components.mongodb.lifecycle :refer [new-mongodb]]
            [cheshire.core :refer [decode]]
            [monger.db :refer [drop-db]]))

(def mongodb (new-mongodb))

(defn setup
  []
  (alter-var-root #'mongodb component/start)
  (drop-db (:db mongodb)))

(defn teardown
  []
  (drop-db (:db mongodb))
  (alter-var-root #'mongodb component/stop))

(against-background [(before :contents (setup)) (after :contents (teardown))]
  (facts "when listing people but no-one exists"
    (let [app (create-handler {:people {:mongodb mongodb}})
          res (app (mock/request :get "/api/people"))]

      (fact "response has a 200 status code"
          (:status res) => 200)
      
      (fact "response has application/json content type"
          (get-in res [:headers "Content-Type"]) => "application/json")

      (fact "response has an empty vector as a result"
          (decode (:body res)) => (contains {"result" []}))))

  (facts "when creating people"
    (let [app (create-handler {:people {:mongodb mongodb}})
          res (app (mock/request :post "/api/people" {:name "gary" :location "home"}))]

      (fact "response has a 201 status code"
        (:status res) => 201)
      
      (fact "response has application/json content type"
        (get-in res [:headers "Content-Type"]) => "application/json")

      (fact "response has an empty vector as a result"
        (get-in res [:headers "Location"]) => #(re-matches #"\/api\/people\/.*" %))))

  (facts "when listing people and people exist"
    (let [app (create-handler {:people {:mongodb mongodb}})
          res (app (mock/request :get "/api/people"))]

      (fact "the response has a 200 status code"
        (:status res) => 200)
      
      (fact "the response has application/json content type"
        (get-in res [:headers "Content-Type"]) => "application/json")

      (fact "the response contains a result with a vector value"
          (decode (:body res)) => (contains {"result" (contains (contains {"location" "home" "name" "gary"}))}))))

  (facts "when reading people"
    (let [app (create-handler {:people {:mongodb mongodb}})
          setup (app (mock/request :post "/api/people" {:name "erin" :location "garden"}))
          path (get-in setup [:headers "Location"])
          id (subs path 12)
          res (app (mock/request :get path))]
      
      (fact "response has a 200 status code"
        (:status res) => 200)
      
      (fact "response has application/json content type"
        (get-in res [:headers "Content-Type"]) => "application/json")
 
      (fact "response has user data"
        (decode (:body res)) => {"result" {"id" id "location" "garden" "name" "erin"}})))

  (facts "when updating people"
    (let [app (create-handler {:people {:mongodb mongodb}})
          setup (app (mock/request :post "/api/people" {:name "erin" :location "garden"}))
          path (get-in setup [:headers "Location"])
          id (subs path 12)
          res (app (mock/request :put path {:location "table"}))]
      
      (fact "response has a 204 status code"
        (:status res) => 204)
      
      (fact "response has application/json content type"
        (get-in res [:headers "Content-Type"]) => "application/json")
     
      (fact "response contains a location header with pointer to the resurce"
        (get-in res [:headers "Location"]) => #(re-matches #"\/api\/people\/.*" %))
     
     (fact "the updated data is available"
       (decode (:body (app (mock/request :get path)))) => (contains {"result" (contains {"location" "table"})}))))

  (facts "when deleting people"
    (let [app (create-handler {:people {:mongodb mongodb}})
          setup (app (mock/request :post "/api/people" {:name "erin" :location "garden"}))
          path (get-in setup [:headers "Location"])
          id (subs path 12)
          res (app (mock/request :delete path))]
      
      (fact "response has a 204 status code"
        (:status res) => 204)
      
      (fact "response has application/json content type"
        (get-in res [:headers "Content-Type"]) => "application/json")
     
     (fact "the updated data is available"
       (:status (app (mock/request :get path))) => 404))))
