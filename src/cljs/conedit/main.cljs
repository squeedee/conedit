(ns conedit.main
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))


(def app-state
  (atom {
         :resources
         [{:name "pivnet-master"}
          {:name "pivnet-develop"}
          ]}))

(declare reconciler)


(defui ResourceItem
  Object
  (render [this]
    (dom/li nil (:name (om/props this)))))

(def resource-item (om/factory ResourceItem {:keyfn :name}))

(defui ResourceList
  Object
  (render [this]
    (dom/ul nil
      (map resource-item (om/props this))
      (dom/button
        #js {:onClick #(om/transact! reconciler '[(add-resource)])}
        "Add"))))

(def resource-list (om/factory ResourceList))

(defui App
  static om/IQuery
  (query [this]
    [:resources])
  Object
  (render [this]
    (dom/div nil (resource-list (:resources (om/props this))))))

; env is more than just the app-atom
(defn read [{:keys [state] :as env} key params]
  (let [data @state]
    (if-let [[_ resources] (find data key)]
      {:value resources}
      {:value :not-found})))

(defn mutate [{:keys [state] :as env} key params]
  {:action #(swap! state update-in [:resources] conj {:name "New!"})}
  )

(def reconciler
  (om/reconciler {:state  app-state
                  :parser (om/parser {:read read :mutate mutate})}))

(om/add-root! reconciler
  App (gdom/getElement "app"))


(comment

  (swap! app-state update-in [:resources] conj {:name "pivnet-lrb"})

  )