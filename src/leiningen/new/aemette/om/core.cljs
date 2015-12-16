(ns {{name}}.core
  (:require [{{name}}.components.app :as app]
            [{{name}}.routes :as routes]
            [{{name}}.controllers.navigation :as nav-con]
            [cljs.core.async :as async :refer [chan]]
            [om.core :as om]
            [{{name}}.history :as history])
  (:require-macros [cljs.core.async.macros :as am :refer [go alt!]]))

(defonce navigation-ch (chan))

(def ^:private debug-state)

(defn install-om [state container comms]
  (om/root app/app state
    {:target container}))

(defn find-app-container []
  (.getElementById js/document "app"))

(defn reinstall-om []
  (install-om debug-state (find-app-container) (:comms @debug-state)))

(defn find-top-level-node []
  (.-body js/document))

(defn app-state []
  (atom (assoc {:navigation-point :landing}
                :comms {:nav navigation-ch
                        :nav-mult (async/mult navigation-ch)})))

(defn nav-handler
  [[navigation-point {:keys [query-params] :as args} :as value] state history]
  (let [previous-state @state]
    (swap! state (partial nav-con/navigated-to history navigation-point args))
    (nav-con/post-navigated-to! history navigation-point args previous-state @state)))

(defn main [state top-level-node history-imp]
  (let [comms       (:comms @state)
        container   (find-app-container)
        nav-tap (chan)]
    (routes/define-routes! state)
    (install-om state container comms)

    (async/tap (:nav-mult comms) nav-tap)

    (go (while true
          (alt!
           nav-tap ([v] (nav-handler v state history-imp))
           ;; Capture the current history for playback in the absence
           ;; of a server to store it
           (async/timeout 10000) (do #_(.log js/console "TODO: print out history: ")))))))


(defn setup! []
  (let [state (app-state)
        top-level-node (find-top-level-node)
        history-imp (history/new-history-imp top-level-node)]
    (set! debug-state state)
    (main state top-level-node history-imp)))

(setup!)
