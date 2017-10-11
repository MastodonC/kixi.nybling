(defproject kixi/kixi.nybling "0.1.11"
  :description "A tiny library that converts formats"
  :url "http://github.com/mastodonc/kixi.nybling"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]                 
                 [com.taoensso/nippy "2.13.0"]
                 [kixi/kixi.log "0.1.4" :exclusions [cheshire]]
                 [com.amazonaws/aws-lambda-java-core "1.0.0"]
                 [com.amazonaws/aws-lambda-java-events "1.0.0"]
                 ;update these in nybling namespace if upgrade
                 [com.cognitect/transit-clj "0.8.300"]
                 [cheshire "5.7.0"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :repositories [["releases" {:url "https://clojars.org/repo"
                              :creds :gpg}]
                 ["snapshots" {:url "https://clojars.org/repo"
                               :creds :gpg}]])
