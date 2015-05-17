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
  "Handles invalid mongoid."
  java.lang.IllegalArgumentException
  (fn [e & args] (warn "illegal mongoid provided" args)))

(defn externalise
  [doc]
  (when doc
    (assoc (get-in doc [:current :doc]) :id (mongoid->external (:_id doc)))))

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
  {:current {:revision 0
             :doc (dissoc doc :id)}
   :previous []})

(defn update-doc
  [current update]
  (merge (:doc current) (dissoc update :id)))

(defn update-versioned-doc
  ([doc] (update-versioned-doc doc (get-in doc [:current :doc])))
  ([{:keys [current previous]} update]
   (-> {}
       (assoc :previous (conj previous current))
       (assoc-in [:current :doc] (update-doc current update))
       (assoc-in [:current :revision] (-> current :revision inc)))))

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
      {:updated true})
    {:updated false}))

(defn delete
  [{:keys [db]} collection id]
  (if-let [_id (external->mongoid id)]
    (let [updated-doc (->> _id
                           (mc/find-map-by-id db collection)
                           update-versioned-doc)
          deleted-doc (assoc-in updated-doc [:current :deleted] true)
          result (mc/update-by-id db collection _id deleted-doc)]
      {:deleted true})
    {:deleted false}))
