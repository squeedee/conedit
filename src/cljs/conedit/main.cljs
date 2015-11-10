(ns conedit.main
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [conedit.parse :as parse]
            [conedit.resource-list :as resource-list]))

(enable-console-print!)

(def app-state
  (atom {
         :resources
         [{:name "pivnet-master"}
          {:name "pivnet-develop"}
          ]}))


(defui App
  static om/IQuery
  (query [this]
    [{:root-resources (om/get-query resource-list/ResourceList)}])
  Object
  (render [this]
    (dom/div nil (resource-list/resource-list (:root-resources (om/props this))))))

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