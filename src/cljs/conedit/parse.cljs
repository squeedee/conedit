(ns conedit.parse)

(defmulti read (fn [env key params] key))

(defmethod read :default [{:keys [state] :as env} key params]
  (let [data @state]
    (if-let [[_ value] (find data key)]
      {:value value}
      {:value :not-found
       })))

(defmethod read :root-resources [{:keys [state] :as env} key params]
  (let [resources (:resources @state)]
    (if resources
      {:value {:resources resources}}
      {:value {:resources []}
       :remote true})))

; {:value {:resources (:value (read env :resources params))}}

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
                 (update-in [:resources] conj (:resource params)))))
   :remote true})

(defmethod mutate 'update-resource [{:keys [state]} _ params]
  {:action #(swap! state update-in [:editor] merge params)})
