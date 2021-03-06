(ns conedit.parse)

(defmulti read (fn [env key params] key))

(defmethod read :default [{:keys [state] :as env} key params]
  (let [data @state]
    (if-let [[_ value] (find data key)]
      {:value value}
      {:value :not-found
       })))

(defmethod read :resources [{:keys [state] :as env} key params]
  (let [resources (:resources @state)]
    (println (pr-str (:target env)))

    (if resources
      (if (:target env)
        {:value resources :remote true}
        {:value resources})
      {:value []
       :remote true})))

(defmulti mutate (fn [env key params] key))

(defmethod mutate 'add-thing [{:keys [state] :as env} key params]
  {:action #(swap! state update-in [:resources] conj {:name "New!"})})

(defmethod mutate 'edit-resource [{:keys [state] :as env} key params]
  {:action #(swap! state assoc :editor {:name ""})})

(defmethod mutate 'save-resource [{:keys [state] :as env} key params]
  {:action #(swap!
             state
             (fn [s]
               (-> s
                 (assoc :editor false)
                 )))
   :remote true})

#_(update-in [:resources] conj params)

(defmethod mutate 'update-resource [{:keys [state]} _ params]
  {:action #(swap! state update-in [:editor] merge params)})
