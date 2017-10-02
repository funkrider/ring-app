(ns ring-app.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.http-response :as response]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.format :refer [wrap-restful-format]]))

(defn handler-old [request]
  (response/ok
    (str "<html><body> your IP is: "
       (:remote-addr request)
       "</body></html>")))

(defn handler [request]
  (response/ok
   {:result (-> request :params :id)}))

(defn wrap-formats [handler]
  (wrap-restful-format
   handler
   {formats [:json-kw :transit-json :transit-msgpack]}))

(defn wrap-nocache [request]
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cahce"))))

(defn -main []
  (jetty/run-jetty
   (-> #'handler wrap-nocache wrap-reload wrap-formats)
   {:port 3000
    :join? false})) ;; join sets server blocking. False is required for REPL
