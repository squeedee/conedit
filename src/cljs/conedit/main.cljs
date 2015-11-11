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
  (atom {:resources []
         :editor false}))


(defn submit [this name]
  (om/transact! this [(list 'save-resource {:name name}) :editor]))

(defui ResourceEditor
  static om/IQuery
  (query [this]
    [:name])
  Object
  (render [this]
    (dom/div nil
      (dom/input #js {:ref "editField" :type "text"
                      :onBlur (fn [e]
                                 (om/transact! this [(list 'update-resource {:name (.. e -target -value)})]))} )
      (dom/button #js {
                      :onClick (fn [_]
                                 (let [node (dom/node this "editField")]
                          (submit this (.-value node))))}
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
      (println (om/props this))
      (dom/div nil
        (resource-list/resource-list root-resources)
        (if-not editor (edit-button this))
        (if editor (resource-editor this))))))

(def reconciler
  (om/reconciler {:state  app-state
                  :parser (om/parser {:read parse/read :mutate parse/mutate})}))

(om/add-root! reconciler
  App (gdom/getElement "app"))

(defn api-request []
  (.send XhrIo "http://localhost:8080/api"
                    (fn [e] (let [response (cljs.reader/read-string (.getResponseText (.-target e)))]
                              (swap! app-state merge response)))
                    "GET" "" #js {}))

(api-request)

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