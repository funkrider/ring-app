(ns ring-app.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn handler [request-map]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str "<html><body> your IP is: "
              (:remote-addr request-map)
              "</body></html>")})

(defn wrap-nocache []
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cahce"))))

(defn -main []
  (jetty/run-jetty
   (-> handler var wrap-nocache wrap-reloady)
   {:port 3000
    :join? false})) ;; join sets server blocking. False is required for REPL
