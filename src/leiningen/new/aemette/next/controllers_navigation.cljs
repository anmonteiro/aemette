(ns {{name}}.controllers.navigation
  (:require [clojure.string :as str]
            [om.next :as om]))

(defmulti navigated-to
  (fn [history-imp navigation-point args reconciler] navigation-point))

(defmulti post-navigated-to!
  (fn [history-imp navigation-point args reconciler]
    navigation-point))

(defn- navigate! [r nav args]
  (om/transact! r `[(~'navigate! {:navigation-point ~nav
                                  :navigation-data ~args})]))

(defn navigated-default [navigation-point args reconciler]
  (navigate! reconciler navigation-point args))

(defmethod navigated-to :default
  [history-imp navigation-point args reconciler]
  (.log js/console "navigation :default")
  (navigated-default navigation-point args reconciler))

(defn post-default [navigation-point args reconciler]
  #_(utils/set-page-title! (or (:_title args)
                          (str/capitalize (name navigation-point)))))

(defmethod post-navigated-to! :default
  [history-imp navigation-point args reconciler]
  (post-default navigation-point args reconciler))
