(ns conedit.main
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [conedit.parse :as parse]
            [conedit.resource-list :as resource-list]
            [cljs.reader]
            )
  (:import [goog.net XhrIo]))

(enable-console-print!)

(def app-state
  (atom {:resources nil
         :editor false}))


(defn submit [this resource]
  (om/transact! this [(list 'save-resource {:resource resource}) :editor]))

(defui ResourceEditor
  static om/IQuery
  (query [this]
    [:name])
  Object
  (render [this]
    (dom/div nil
      (dom/input #js {:ref "editField" :type "text"
                      :onBlur (fn [e]
                                 (om/transact!
                                   this
                                   [(list 'update-resource {:name (.. e -target -value)})]))} )
      (dom/button #js {:onClick (fn [_]
                                  (submit this (om/props this)))}
        "Submit"))))


(def resource-editor (om/factory ResourceEditor))

(defn edit-button [this]
  (dom/button
    #js {:onClick #(om/transact! this '[(edit-resource)])}
    "Add"))

(defui App
  static om/IQuery
  (query [this]
    [{:root-resources (om/get-query resource-list/ResourceList)}
     {:editor (om/get-query ResourceEditor)}])
  Object
  (render [this]
    (let [{:keys [editor root-resources]} (om/props this)]
      (dom/div nil
        (resource-list/resource-list root-resources)
        (if-not editor (edit-button this))
        (if editor (resource-editor (:editor (om/props this))))))))

(defn api-request [data-to-send response-handler]
  (.send XhrIo "http://localhost:8080/api"
    (fn [e] (let [response (cljs.reader/read-string (.getResponseText (.-target e)))]
              (response-handler response)))
    "GET" (pr-str (:remote data-to-send))
    #js {}))


(def reconciler
  (om/reconciler {:state   app-state
                  :parser  (om/parser {:read parse/read :mutate parse/mutate})
                  :send    (fn [data-to-send response-handler]
                             (.log js/console (pr-str [:send data-to-send]))
                             (api-request data-to-send response-handler))
                  :merge-tree merge
                  :remotes [:remote]}))


(om/add-root! reconciler
  App (gdom/getElement "app"))



;(api-request)

(comment

  (swap! app-state update-in [:resources] conj {:name "pivnet-lrb"})

  (def p (om/parser {:read parse/read :mutate parse/mutate}))

  (p {:state app-state} [:foo] true)

  @app-state

 (js/alert "foo")

  (.send XhrIo "http://localhost:8080/api"
    (fn [e] (let [response (cljs.reader/read-string (.getResponseText (.-target e)))]
              response))
    "GET" "" #js {})

  )