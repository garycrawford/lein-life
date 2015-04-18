(ns leiningen.new.common-templates
  (:require [clojure.string :as string])
  )

(defn construct-template
  [lines]
  (->> lines
       (partition 2)
       (map (fn [[line check-fn]] (when (check-fn) line)))
       (filter (complement nil?))
       (string/join \newline)))

(def always (constantly true))
(def mongodb? (partial = :mongodb))
(def not-nil? (complement nil?))

(defn compose-api-proj
  [{:keys [docker-name dockerised-svr ns-name] :as this} {:keys [db]}]
  (let [template (-> ["%1s:"                                                     always
                      "  build: ./%3$s"                                          always
                      "  ports:"                                                 always
                      "   - \"4321:1234\""                                       always
                      "   - \"31313:31313\""                                     always
                      "  volumes:"                                               always
                      "   - ./%3$s:/usr/src/app"                                 always
                      "  links:"                                                 always
                      "   - metrics"                                             always
                      "   - db"                                                  #(mongodb? db)
                      "  hostname: \"%2s\""                                      always
                      "  environment:"                                           always
                      "     MONGODB_URI: mongodb://192.168.59.103/%3$s"          #(mongodb? db)
                      "     METRICS_HOST: 192.168.59.103"                        always
                      "     METRICS_PORT: 2003"                                  always
                      "     APP_NAME: %3$s"                                      always
                      "  command: lein repl :headless :host 0.0.0.0 :port 31313" always
                      ""                                                         always]
                     construct-template)]
    (format template docker-name dockerised-svr ns-name)))

(defn compose-site-proj
  [{:keys [docker-name dockerised-svr ns-name api-docker-name] :as this} {:keys [db]}]
  (let [template (-> ["%1s:"                                                     always
                      "  build: ./%3$s"                                          always
                      "  ports:"                                                 always
                      "   - \"1234:1234\""                                       always
                      "   - \"21212:21212\""                                     always
                      "  volumes:"                                               always
                      "   - ./%3$s:/usr/src/app"                                 always
                      "  links:"                                                 always
                      (str "   - " api-docker-name)                              #(not-nil? api-docker-name)
                      "   - metrics"                                             always
                      "   - db"                                                  #(mongodb? db)
                      "  hostname: \"%2s\""                                      always
                      "  environment:"                                           always
                      "     MONGODB_URI: mongodb://192.168.59.103/%3$s"          #(mongodb? db)
                      "     METRICS_HOST: 192.168.59.103"                        always
                      "     METRICS_PORT: 2003"                                  always
                      "     APP_NAME: %3$s"                                      always
                      "  command: lein repl :headless :host 0.0.0.0 :port 21212" always
                      ""                                                         always]
                     construct-template)]
    (format template docker-name dockerised-svr ns-name)))

(defn compose-deps
  [{:keys [db]}]
  (-> ["metrics:"                                                 always
       "  image: garycrawford/grafana_graphite:0.0.1"             always
       "  volumes:"                                               always
       "   - ./dashboards:/src/dashboards"                        always
       "  ports:"                                                 always
       "   - \"80:80\""                                           always
       "   - \"2003:2003\""                                       always
       "db:"                                                      #(mongodb? db)
       "   image: mongo:3.0.1"                                    #(mongodb? db)
       "   ports:"                                                #(mongodb? db)
       "   - \"27017:27017\""                                     #(mongodb? db)]
      construct-template))
