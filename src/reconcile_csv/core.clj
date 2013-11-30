(ns reconcile-csv.core
  (:use [ring.adapter.jetty]
        [ring.middleware.params]
        [compojure.core :only (defroutes GET POST)]
        [ring.util.codec :only (form-decode)]
        [clojure.tools.nrepl.server 
         :only (start-server stop-server)]
        [clojure.string :only (lower-case split)]
        )
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.data.json :as json]
            [fuzzy-string.core :as fuzzy]
            [csv-map.core :as csv-map])
  (:gen-class))

(defonce server (start-server :port 7888))
(def data (atom (list)))
(def config (atom {}))

(defn hello [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World\n"})

(defn get-data []
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str (vec @data)) })

(defn four-o-four []
  {:status 404
   :headers {"Content-Type" "text/html"}
   :body "404 not found"})

(defn service-metadata []
  (do (print "getting metadata") 
  {:name "CSV Reconciliation service"
   :identifierSpace "http://localhost:8000/"
   :schemaSpace "http://localhost:8000/"
   :defaultTypes []
   :view  {
            :url "http://localhost:8000/view/{{id}}"
            }
   :preview {
             :url "http://localhost:8000/view/{{id}}"
             :width 510
             :height 400
             }

   })
  )

(def lcase (memoize lower-case))

(defn score [^clojure.lang.PersistentVector query 
             ^clojure.lang.PersistentArrayMap row]
  "calculates the score for a query - which at this stage is a vector of vectors..."
    (assoc row :score (reduce * 
                              (map (fn [x] (fuzzy/dice 
                                           (lcase (second x)) 
                                           (lcase (get row (first x)))))
                                   query))))

(defn score-response [^clojure.lang.PersistentArrayMap x]
  {:id (get x (:id-column @config))
   :name (get x (:search-column @config))
   :score (:score x)
   :match (if (= (:score x) 1) true false)
   :type [{"name" "CSV-recon"
           "id" "/csv-recon"}]
   }
  )

(defn extend-query [query properties]
  (loop [q query p properties]
    (if (first p)
      (let [tp (first p)]
        (recur (assoc q (:pid tp) (:v tp)) (rest p)))
      q)))

(defn scores [q json?]
  (let [query {(:search-column @config)
               (if json? (:query q) q)}
        limit (if (:limit q) (:limit q) 5)
        query (if (:properties q)
                (extend-query query (:properties q))
                query)
        query (vec query)
        score (partial score query)]
   (vec (map score-response
             (take limit 
                   (sort-by (fn [x] (- (:score x)))
                            (map score @data)))))))
                       

(defn reconcile-param [query]
  (let [q (try (json/read-str query :key-fn keyword)
               (catch Exception e query))
        j (if (:query q) true false)
        ]   
  {:result (scores q j)}
  ))

(defn reconcile-params [queries]
  (let [queries (json/read-str queries :key-fn keyword)]
    (do (println queries)
        (zipmap (keys queries)
                (pmap reconcile-param (vals queries))))))

(defn encapsulate-jsonp [callback d]
  (str callback "(" d ")"))

(defn reconcile [request]
  (let [params (:params request)
        d (json/write-str (if (:query params)
                                (reconcile-param 
                                 (:query params))
                                (if (:queries params)
                                  (reconcile-params 
                                   (:queries params))
                                  (service-metadata))))]
    (do (println request)
  {:status 200
   :headers {"Content-Type" "application/javascript"}
   :body (if (:callback params) 
           (encapsulate-jsonp 
            (:callback params) d)
           d)                                                  
   })))

(defn view [id]
  (let [o (first (filter #(= (get % (:id-column @config)) id) @data))]
    (if (not o) 
      (four-o-four)
      (str "<html><head>"
           "<style>\n"
           "dl { width: 500px; }\n"
           "dt { font-weight: bold }\n"
           "dd { }\n"
           "dt, dd {display: inline-block;}\n"
           "</style>"
           "</head><body><dl>"
           (clojure.string/join 
            "\n"
            (map #(str "<dt>" (first %) "</dt><dd>" (second %) "</dd><br/>") o))
           "</dl></body></html"))))

(defroutes routes 
  (GET "/" [] (hello nil))
  (GET "/reconcile" [:as r] (reconcile r))
  (POST "/reconcile" [:as r] (reconcile r))
  (GET "/view/:id" [id] (view id))
  (GET "/data" [] (get-data))
  (route/not-found (four-o-four)))

(def app
   (handler/api routes))

(defn -main [file search-column id-column]
  (swap! data (fn [x file] (csv-map/parse-csv (slurp file))) file)
  (swap! config (fn [x y] (assoc x :search-column y)) search-column)
  (swap! config (fn [x y] (assoc x :id-column y)) id-column)
  (run-jetty app {:port 8000 :join? false}))