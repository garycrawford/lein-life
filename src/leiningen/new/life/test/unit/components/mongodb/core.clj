(ns {{ns-name}}.unit.components.mongodb.core
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.components.mongodb.core :refer :all]))

(def mongo-id (monger.conversion/to-object-id "507f191e810c19729de860ea"))
(def external-id "yNyaoWeKWVINWqvaM9bw")
(def dummy-doc {:_id mongo-id})
(def dummy-query {:id external-id})

(fact "to support obscuring mongodb id's from the outside world"
  (fact "the mongodb id can be encrypted"
      (mongoid->external mongo-id) => external-id)

  (fact "an encrypted mongodb id can be decrypted"
      (external->mongoid external-id) => mongo-id)
  
  (fact "mongodb docs have _id removed and id appended"
      (externalise dummy-doc) => {:id external-id})

  (fact "externalise can handle nil query results"
      (externalise nil) => nil)

  (fact "incoming queries will have :id replaced with :_id"
      (marshall-query dummy-query) => (contains {:_id mongo-id})))

(fact "to support saving all historical states of data"
  (fact "incoming queries will be changed to filter deleted docs")
      (marshall-query dummy-query) => (contains {:current.deleted {"$exists" false}})  

  (fact "incoming queries will be changed to filter deleted docs")
      (marshall-query dummy-query) => (contains {:current.deleted {"$exists" false}})

  (fact "new documents can be versioned"
      (version-doc {}) => (contains {:current (contains {:revision 0})}))
      
  (fact "new versioned documents have no history"
      (version-doc {}) => (contains {:previous []}))

  (fact "updated versioned docs have current version number incremented"
    (let [prev-doc (version-doc {:name "gary"})]
      (update-versioned-doc prev-doc {:name "erin"}) => (contains {:current (contains {:revision 1})})))

  (fact "updated versioned docs current data updated"
    (let [prev-doc (version-doc {:name "gary"})]
      (update-versioned-doc prev-doc {:name "erin"}) => (contains {:current (contains {:doc {:name "erin"}})})))

  (fact "updated versioned docs will not have an :id"
    (let [prev-doc (version-doc {:id "id" :name "gary"})]
      (update-versioned-doc prev-doc {:name "erin"}) =not=> (contains {:current (contains {:doc {:id "id"}})})))

  (fact "updated versioned docs have historical data added to previous"
    (let [prev-doc (version-doc {:name "gary"})]
      (update-versioned-doc prev-doc {:name "erin"}) => (contains {:previous (just (contains {:doc {:name "gary"} :revision 0}))})))

  (fact "previous versioned docs will not have an :id"
    (let [prev-doc (version-doc {:id "id" :name "gary"})]
      (update-versioned-doc prev-doc {:name "erin"}) => (contains {:previous (just (just {:doc {:name "gary"} :revision 0}))}))))
