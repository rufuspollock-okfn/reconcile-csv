(defproject reconcile-csv "0.1.0-SNAPSHOT"
  :description "A Reconciliation service, connecting CSV's with OpenRefine"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
    [org.clojure/tools.nrepl "0.2.3"]
    [ring/ring-core "1.2.0"]
    [ring/ring-jetty-adapter "1.2.0"]
    [compojure "1.1.6"]
    [org.clojure/data.json "0.2.3"]
    [fuzzy-string "0.1.2-SNAPSHOT"]
    [csv-map "0.1.0-SNAPSHOT"]
  ]
  :plugins [[lein-ring "0.8.7"]]
  :main reconcile-csv.core
  )
  
