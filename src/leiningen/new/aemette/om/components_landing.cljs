(ns {{name}}.components.landing
  (:require [om.core :as om]
            [om.dom :as dom]))

(defn home [state owner]
  (reify
    om/IDisplayName (display-name [_] "Homepage")
    om/IRender
    (render [_]
      (dom/div nil
        (dom/p nil "Landing page")))))
