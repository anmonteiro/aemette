(ns {{name}}.components.app
  (:require [{{name}}.components.landing :as landing]
            [om.next :as om :refer-macros [defui ui]]
            [om.dom :as dom]))

(def nav->component
  {:landing landing/Home})

(def nav->factory
  (zipmap (keys nav->component)
          (map om/factory (vals nav->component))))

(defui App
  static om/IQueryParams
  (params [this]
    {:dom-com/query []})
  static om/IQuery
  (query [this]
   '[:navigation-point {:dom-com/props ?dom-com/query}])
  Object
  (componentWillUpdate [this next-props _]
    (let [prev-nav (:navigation-point (om/props this))
          new-nav (:navigation-point next-props)]
      (when (not= prev-nav new-nav)
        (let [new-query
                (or
                  (-> new-nav
                      nav->component
                      om/get-query)
                      ;; TODO: this might not be necessary if all components
                      ;;       have queries
                      [])]
          (om/set-query! this {:params {:dom-com/query new-query}})))))
  (render [this]
    (let [{:keys [navigation-point dom-com/props]} (om/props this)
          dominant-component (navigation-point nav->factory)]
        (dom/div nil
          (dominant-component props)))))
