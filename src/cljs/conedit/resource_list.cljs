(ns conedit.resource-list

  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

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
      (map resource-item (:resources (om/props this))))))

(def resource-list (om/factory ResourceList))
