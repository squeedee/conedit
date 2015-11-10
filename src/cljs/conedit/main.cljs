(ns conedit.main
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [conedit.parse :as parse]
            [conedit.resource-list :as resource-list]))

(enable-console-print!)

(def app-state
  (atom {
         :resources [
                     {:name "pivnet-master"}
                     {:name "pivnet-develop"}]
         :editing   false}))

(defui ResourceEditor
  Object
  (render [this]
    (dom/div nil "editor!")))

(def resource-editor (om/factory ResourceEditor))

(defn edit-button [this]
  (dom/button
    #js {:onClick #(om/transact! this '[(edit-resource)])}
    "Add"))

(defui App
  static om/IQuery
  (query [this]
    [{:root-resources (om/get-query resource-list/ResourceList)} :editing])
  Object
  (render [this]
    (let [{:keys [editing root-resources]} (om/props this)]
      (dom/div nil
        (resource-list/resource-list root-resources)
        (if-not editing (edit-button this))
        (if editing (resource-editor))))))

(def reconciler
  (om/reconciler {:state  app-state
                  :parser (om/parser {:read parse/read :mutate parse/mutate})}))

(om/add-root! reconciler
  App (gdom/getElement "app"))


(comment

  (swap! app-state update-in [:resources] conj {:name "pivnet-lrb"})

  (def p (om/parser {:read parse/read :mutate parse/mutate}))

  (p {:state app-state} [{:root-resources [{:resources [:name]}]}] :remote)

  )