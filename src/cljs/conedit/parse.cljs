(ns conedit.parse
  )

(defmulti read (fn [env key params] key))

(defmethod read :default [{:keys [state] :as env} key params]
  (let [data @state]
    (if-let [[_ value] (find data key)]
      {:value value}
      {:value :not-found})))

(defmethod read :root-resources [{:keys [state] :as env} key params]
  {:value {:resources (:value (read env :resources params))}})


(defn mutate [{:keys [state] :as env} key params]
  {:action #(swap! state update-in [:resources] conj {:name "New!"})})