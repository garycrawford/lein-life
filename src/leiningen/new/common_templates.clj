(ns leiningen.new.common-templates
  (:require [clojure.string :as string]
            [camel-snake-kebab.core :refer [->PascalCase]]
            [clostache.parser :refer [render]]))

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

(defn site-vals
  [{:keys [parent-name ns-name api-name]}]
  (merge {:db-name (string/replace parent-name "-" "_")
          :ns-name ns-name
          :path (string/replace ns-name "-" "_")
          :docker-name (string/replace ns-name "-" "")
          :dockerised-svr (str (->PascalCase ns-name) "DevSvr")}
         (when api-name 
          {:api-docker-name (string/replace api-name "-" "")})))

(defn compose-api-proj
  [this {:keys [db]}]
  (let [vals (site-vals this)
        template (-> ["{{docker-name}}:"                                         always
                      "  build: ./{{path}}"                                      always
                      "  ports:"                                                 always
                      "   - \"4321:1234\""                                       always
                      "   - \"31313:31313\""                                     always
                      "  volumes:"                                               always
                      "   - ./{{path}}:/usr/src/app"                             always
                      "  links:"                                                 always
                      "   - metrics"                                             always
                      "   - db"                                                  #(mongodb? db)
                      "  hostname: \"{{dockerised-svr}}\""                       always
                      "  environment:"                                           always
                      "     MONGODB_URI: mongodb://192.168.59.103/{{db-name}}"   #(mongodb? db)
                      "     METRICS_HOST: 192.168.59.103"                        always
                      "     METRICS_PORT: 2003"                                  always
                      "     APP_NAME: {{ns-name}}"                               always
                      "  command: lein repl :headless :host 0.0.0.0 :port 31313" always
                      ""                                                         always]
                     construct-template)]
    (render template vals)))

(defn compose-site-proj
  [{:keys [api-name] :as this} {:keys [db]}]
  (let [vals (site-vals this)
        template (-> ["{{docker-name}}:"                                         always
                      "  build: ./{{path}}"                                      always
                      "  ports:"                                                 always
                      "   - \"1234:1234\""                                       always
                      "   - \"21212:21212\""                                     always
                      "  volumes:"                                               always
                      "   - ./{{path}}:/usr/src/app"                             always
                      "  links:"                                                 always
                      "   - {{api-docker-name}}"                                 #(not-nil? api-name)
                      "   - metrics"                                             always
                      "   - db"                                                  #(mongodb? db)
                      "  hostname: \"{{dockerised-svr}}\""                       always
                      "  environment:"                                           always
                      "     MONGODB_URI: mongodb://192.168.59.103/{{db-name}}"   #(mongodb? db)
                      "     METRICS_HOST: 192.168.59.103"                        always
                      "     METRICS_PORT: 2003"                                  always
                      "     APP_NAME: {{ns-name}}"                               always
                      "  command: lein repl :headless :host 0.0.0.0 :port 21212" always
                      ""                                                         always]
                     construct-template)]
    (render template vals)))

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
       "   - \"27017:27017\""                                     #(mongodb? db)
       "   command: --smallfiles"                                 #(mongodb? db)]
      construct-template))
