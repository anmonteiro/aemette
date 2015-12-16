(ns {{name}}.components.landing
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui Home
  Object
  (render [_]
    (dom/div nil
      (dom/p nil "Landing page"))))
