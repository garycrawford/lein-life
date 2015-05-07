(defproject {{ns-name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

{{{project-deps}}}
  
  :profiles {:uberjar {:aot :all             
                       :main {{ns-name}}.zygote}}

  :aliases {"omni" ["do"
                    ["clean"]
                    ["with-profile" "production" "deps" ":tree"]
                    ["ancient"]
                    ["kibit"]
                    ["bikeshed" "-m" "120"]
                    ["eastwood"]]
            "slamhound" ["run" "-m" "slam.hound"]})
