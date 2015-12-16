(ns {{name}}.parser
  (:require [om.next :as om]))

;; =============================================================================
;; Reads

(defmulti read om/dispatch)

(defmethod read :default
  [{:keys [state]} k _]
  (let [st @state]
    {:value (get st k)}))

(defmethod read :dom-com/props
  [{:keys [parser query ast target] :as env} k params]
  {:value (parser env query target)})

;; =============================================================================
;; Mutations

(defmulti mutate om/dispatch)

(defmethod mutate 'navigate!
  [{:keys [state] :as env} _ {:keys [navigation-point navigation-data] :as params}]
  {:value {:keys [:navigation-point :navigation-data]}
   :action #(swap! state assoc :navigation-point navigation-point
                               :navigation-data navigation-data)})
