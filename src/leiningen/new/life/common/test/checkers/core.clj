(ns {{ns-name}}.checkers.core
  (:require [ring.util.response :refer [get-header]]
            [ring.util.mime-type :refer [default-mime-types]]))

(defn location?
  [expected-location]
  (fn [response]
    (let [actual-location (get-header response "Location")]
      (= expected-location actual-location))))

(defn status?
  [expected-status]
  (fn [response]
    (let [actual-status (:status response)]
      (= expected-status actual-status))))

(defn content-type?
  [expected-content-name]
  (fn [response]
    (let [expected-content-type (get default-mime-types expected-content-name)
          actual-content-type (get-header response "Content-Type")]
      (= expected-content-type actual-content-type))))

(defn result?
  [expected-result]
  (fn [response] (-> response
                     (get-in [:body :result])
                     (= expected-result))))
