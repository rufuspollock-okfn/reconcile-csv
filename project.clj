(defproject reconcile-csv "0.1.2"
  :description "A Reconciliation service, connecting CSV's with OpenRefine"
  :url "http://okfnlabs.org/reconcile-csv"
  :license {:name "BSD 2-Clause"
            :file "LICENSE" }
  :dependencies [[org.clojure/clojure "1.5.1"]
    [org.clojure/tools.nrepl "0.2.3"]
    [ring/ring-core "1.2.0"]
    [ring/ring-jetty-adapter "1.2.0"]
    [compojure "1.1.6"]
    [org.clojure/data.json "0.2.3"]
    [fuzzy-string "0.1.3"]
    [csv-map "0.1.0"]
  ]
  :plugins [[lein-ring "0.8.7"]]
  :main reconcile-csv.core
  )
  
