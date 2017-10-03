(ns ring-app.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.http-response :as response]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [compojure.core :as compojure]))

;; curl -H "Content-Type: application/json" -X POST -d "{\"id\" :1}" localhost:3000/json
;; will result in {"result" 1}

(defn response-handler [request]
  (response/ok
    (str "<html><body> your IP is: "
       (:remote-addr request)
       "</body></html>")))

(defn api-call-handler [request]
  (response/ok
   {:result (-> request :params :id)}))

(compojure/defroutes handler
  (compojure/GET "/" request response-handler)
  (compojure/GET "/:id" [id] (str "<p>the id is: " id "</p>"))
  (compojure/POST "/json" [id] (response/ok {:result id})))    ;; id is expected to be a json encoded param at the root of the json e.g. {"id" :1} etc.

(defn wrap-formats [handler]
  (wrap-restful-format
   handler
   {:formats [:json-kw :transit-json :transit-msgpack]}))

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
