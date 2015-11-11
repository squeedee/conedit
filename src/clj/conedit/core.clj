(ns conedit.core
  (:require [aleph.http :as http]
            [ring.middleware.resource :as ring-resources]
            [ring.util.response :as ring-response]
            [bidi.ring]))


(defn default-handler [req]
  {:status  200
   :headers {"content-type" "text/plain"}
   :body    "hello1!"})

(def bidi-handler
  (bidi.ring/make-handler
    ["/"
     {"" (fn [r] (ring-response/resource-response "index.html" {:root "public"}))}]))


(defn root-handler [] (fn [request]
                        (or (ring-resources/resource-request request "public" {:loader nil})
                          (bidi-handler request))))

(defn live-handler [req]
  ((root-handler) req))


(defn start-server [env]
  (let [{:keys [dev port] :or {port 8080 dev true}} env]
    (http/start-server
      (if dev
        live-handler
        (root-handler))
      {:port port})))

(comment
  (start-server {})



  *e
  )