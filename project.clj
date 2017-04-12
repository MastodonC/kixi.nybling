(defproject kixi/kixi.nybling "0.1.5-SNAPSHOT"
  :description "A tiny library that converts formats"
  :url "http://github.com/mastodonc/kixi.nybling"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cheshire "5.7.0"]
                 [com.taoensso/nippy "2.13.0"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :repositories [["releases" {:url "https://clojars.org/repo"
                              :creds :gpg}]
                 ["snapshots" {:url "https://clojars.org/repo"
                               :creds :gpg}]])
