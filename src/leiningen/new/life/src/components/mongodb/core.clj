(ns {{ns-name}}.components.mongodb.core
  (:require [monger.collection :as mc]
            [monger.operators :refer [$inc $exists]]
            [monger.conversion :refer [to-object-id]]
            [taoensso.timbre :refer [info warn]]
            [robert.hooke :refer [prepend append]]
            [hashids.core :refer [encode-hex decode-hex]]
            [clojure.core.incubator :refer [dissoc-in]]
            [dire.core :refer [with-handler!]]))

(def hashids-opts {:salt "this is my salt"})

(defn mongoid->external
  [mongoid]
  (encode-hex hashids-opts (.toStringMongod mongoid)))

(defn external->mongoid
  [external-id]
  (let [[left-long right-long] (decode-hex hashids-opts external-id)]
    (to-object-id
      (apply str left-long right-long))))

(with-handler! #'external->mongoid
  "Here's an optional docstring about the handler."
  java.lang.IllegalArgumentException
  (fn [e & args] (warn "illegal mongoid provided" args)))

(defn externalise
  [doc]
  (when doc
    (assoc (:current doc) :id (mongoid->external (:_id doc)))))

(defn marshall-query
  [{:keys [id] :as query}]
  (let [without-id (-> query
                       (dissoc :id)
                       (assoc :current.deleted {$exists false}))]
    (if id
      (assoc without-id :_id (external->mongoid id))
      without-id)))

(defn version-doc
  [doc]
  {:current (-> doc (assoc :revision 0) (dissoc :id))
   :previous []})

(defn update-versioned-doc
  [{:keys [current previous]} update-doc]
  (-> {}
      (assoc :previous (conj previous current))
      (assoc :current (merge current (dissoc update-doc :id)))
      (update-in [:current :revision] inc)))

(defn find-one-by-query
  [{:keys [db]} collection query]
  (let [marshalled-query (marshall-query query)
        query-result (mc/find-one-as-map db collection marshalled-query {:previous 0})]
    (externalise query-result)))

(defn find-by-id
  [mongodb collection id]
  (find-one-by-query mongodb collection {:id id}))

(defn find-by-query
  [{:keys [db]} collection query]
  (let [marshalled-query (marshall-query query)
        result (mc/find-maps db collection marshalled-query {:previous 0})]
    (map externalise result)))

(defn insert
  [{:keys [db]} collection doc]
  (let [versioned-doc (version-doc doc)
        {_id :_id} (mc/insert-and-return db collection versioned-doc)]
    {:id (mongoid->external _id)}))

(defn update
  [{:keys [db]} collection {:keys [id] :as doc}]
  (if-let [_id (external->mongoid id)]
    (let [old-versioned-doc (mc/find-map-by-id db collection _id)
          new-versioned-doc (update-versioned-doc old-versioned-doc doc)
          result (mc/update-by-id db collection _id new-versioned-doc)]
      {:count (.getN result)})
    {:count 0}))

(defn delete
  [mongodb collection id]
  (update mongodb collection {:id id :deleted true}))
