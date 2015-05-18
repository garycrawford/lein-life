(ns {{ns-name}}.unit.components.graphite.lifecycle
  (:require [midje.sweet :refer :all]
            [{{ns-name}}.components.graphite.lifecycle :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [com.gfredericks.test.chuck.generators :as gen']))

(def uppercase (map char (range 65 91)))
(def lowercase (map char (range 97 123)))
(def numeric   (map char (range 48 58)))
(def alphanumeric (concat uppercase lowercase numeric))

(defn prop-regex-matches-characters
  [r & character-sets]
  (let [characters (-> character-sets flatten)]
    (prop/for-all [v (gen'/string-from-regex r)]
      (every? identity (map #(some #{%} characters) (seq v))))))

(fact "app-name-regex should only allow alphanumeric and dashes"
  (let [property (prop-regex-matches-characters app-name-regex alphanumeric \-)]
    (tc/quick-check 100 property)) => (contains {:result true}))

(fact "hostname should only allow alphanumeric"
  (let [property (prop-regex-matches-characters hostname-regex alphanumeric)]
    (tc/quick-check 100 property)) => (contains {:result true}))

(fact "a valid config will not generate an error"
  (let [config (metrics-reporter-config)]
    config => (contains
                {:prefix "stats.timers.{{ns-name}}.{{dockerised-svr}}"
                 :port   2003
                 :host   "{{docker-ip}}"})))
