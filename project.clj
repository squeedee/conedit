(defproject conedit "0.1.0-SNAPSHOT"
  :description "Concourse Pipeline Editor"
  :source-paths ["src/clj" "src/cljs"]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.145"]
                 [org.omcljs/om "1.0.0-alpha14"]
                 [figwheel-sidecar "0.4.0" :scope "provided"]
                 [aleph "0.4.1-beta2"]
                 [bidi "1.21.1"]
                 [ring/ring-core "1.4.0"]])