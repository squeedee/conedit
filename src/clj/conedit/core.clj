(ns conedit.core
  (:require [aleph.http :as http]
            [ring.middleware.resource :as ring-resources]
            [ring.util.response :as ring-response]
            [bidi.ring]
            [om.next.server]))


(defn default-handler [req]
  {:status  200
   :headers {"content-type" "text/plain"}
   :body    "hello1!"})


(def database
  (atom
    {:resources [
                 {:name "pivnet-master"}
                 {:name "pivnet-develop"}]}))

(defmulti mutatef (fn [env key params] key))

(defmethod mutatef 'save-resource [{:keys [state] :as env} key params]
  {:action #(swap! state update-in [:resources] conj params)})

(defmulti readf (fn [env key params] key))

(defmethod readf :resources [{:keys [state] :as env} key params]
  (let [resources (:resources @state)]
    {:value resources}))

(def parser (om.next.server/parser
              {:read readf :mutate mutatef}))

(comment
  (parser {:state database} [{:resources [:name]}])
  (pr-str (parser {:state database} (read-string (slurp (:body r)))))

  )

(def bidi-handler
  (bidi.ring/make-handler
    ["/"
     {""    (fn [r] (ring-response/resource-response "index.html" {:root "public"}))
      "api" (fn [r]
              {:status  200
               :headers {"content-type" "text/plain"}
               :body    (pr-str (parser {:state database} (read-string (slurp (:body r)))))})}]))

(defn root-handler []
  (fn [request]
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

(defn -main []
  (println "Launching!")
  (start-server {:dev false :port 3000}))

(comment
  (start-server {})

  *e
  )