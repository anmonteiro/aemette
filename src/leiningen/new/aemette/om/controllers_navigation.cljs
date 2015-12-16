(ns {{name}}.controllers.navigation
  (:require [clojure.string :as str]))

(defmulti navigated-to
  (fn [history-imp navigation-point args state] navigation-point))

(defmulti post-navigated-to!
  (fn [history-imp navigation-point args previous-state current-state]
    navigation-point))


(defn navigated-default [navigation-point args state]
  (-> state
      (assoc :navigation-point navigation-point
             :navigation-data args)))

(defmethod navigated-to :default
  [history-imp navigation-point args state]
  (navigated-default navigation-point args state))

(defn post-default [navigation-point args]
  ;; set e.g. page title here
  )

(defmethod post-navigated-to! :default
  [history-imp navigation-point args previous-state current-state]
  (post-default navigation-point args))
