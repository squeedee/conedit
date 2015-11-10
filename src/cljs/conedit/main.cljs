(ns conedit.main
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)


(def app-state
  (atom {
         :resources
         [{:name "pivnet-master"}
          {:name "pivnet-develop"}
          ]}))

(defui ResourceItem
  static om/IQuery
  (query [this]
    [:name])
  Object
  (render [this]
    (dom/li nil (str (:name (om/props this)) " " (:foo (om/props this))))))

(def resource-item (om/factory ResourceItem {:keyfn :name}))

(defui ResourceList
  static om/IQuery
  (query [this]
    [{:resources (om/get-query ResourceItem)}])
  Object
  (render [this]
    (dom/ul nil
      (map resource-item (:resources (om/props this)))
      (dom/button
        #js {:onClick #(om/transact! reconciler '[(add-resource)])}
        "Add"))))

(def resource-list (om/factory ResourceList))

(defui App
  static om/IQuery
  (query [this]
    [{:root-resources (om/get-query ResourceList)}])
  Object
  (render [this]
    (dom/div nil (resource-list (:root-resources (om/props this))))))


(defmulti read (fn [env key params] key))

(defmethod read :default [{:keys [state] :as env} key params]
  (let [data @state]
    (if-let [[_ value] (find data key)]
      {:value value}
      {:value :not-found})))

(defmethod read :root-resources [{:keys [state] :as env} key params]
  {:value {:resources (:value (read env :resources params))}})


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