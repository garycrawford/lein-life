{:dev {:source-paths ["dev"]
       :plugins [[lein-ancient "0.6.3"]
                 [jonase/eastwood "0.2.1"]
                 [lein-bikeshed "0.2.0"]
                 [lein-kibit "0.0.8"]
                 [lein-environ "1.0.0"]
                 [lein-midje "3.1.3"]]
       :dependencies [[org.clojure/tools.namespace "0.2.10"]
                      [slamhound "1.5.5"]
                      [com.cemerick/pomegranate "0.3.0" :exclusions [org.codehaus.plexus/plexus-utils]]
                      [prone "0.8.1"]
                      [midje "1.6.3"]
                      [org.clojure/test.check "0.7.0"]
                      [com.gfredericks/test.chuck "0.1.16"]
                      [kerodon "0.6.0"]]
       :env {:metrics-host "192.168.59.103"
             :metrics-port 2003
             :app-name "{{ns-name}}"
             :hostname "{{dockerised-svr}}"
             {{{dev-profile_}}}}
       :ring {:stacktrace-middleware prone.middleware/wrap-exceptions}}}
