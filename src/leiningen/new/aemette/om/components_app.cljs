(ns {{name}}.components.app
  (:require [{{name}}.components.landing :as landing]
            [om.core :as om]
            [om.dom :as dom]))

(def nav->component
  {:landing landing/home})


(defn app* [state owner opts]
  (reify
    om/IDisplayName (display-name [_] "App")
    om/IRender
    (render [_]
      (let [dom-com (nav->component (:navigation-point state))]
        (dom/div nil
          (om/build dom-com state))))))

(defn app [state owner opts]
  (reify
    om/IDisplayName (display-name [_] "App Wrapper")
    om/IRender (render [_] (om/build app* state {:opts opts}))))
