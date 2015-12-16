(ns {{name}}.core
  (:require [cljs.core.async :as async :refer [chan close!]]
            [{{name}}.components.app :as app]
            [{{name}}.controllers.navigation :as nav-con]
            [{{name}}.history :as history]
            [{{name}}.parser :as p]
            [{{name}}.routes :as routes]
            [om.next :as om])
  (:require-macros [cljs.core.async.macros :as am :refer [go alt!]]))

(defonce navigation-ch (chan))

(def ^:private debug-state)

(def reconciler)
(def parser (om/parser {:read p/read :mutate p/mutate}))

(defn install-om [state container comms]
  (let [rec (om/reconciler {:state state
                            :parser parser})]
     (set! reconciler rec)
     (om/add-root! reconciler app/App container)))

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
  [[navigation-point {:keys [query-params] :as args} :as value] history]
  ;; TODO: make sure reconciler is bound
  (nav-con/navigated-to history navigation-point args reconciler)
  (nav-con/post-navigated-to! history navigation-point args reconciler))

(defn main [state top-level-node history-imp]
  (let [comms       (:comms @state)
        container   (find-app-container)
        nav-tap (chan)]
    (routes/define-routes! state)
    (install-om state container comms)

    (async/tap (:nav-mult comms) nav-tap)

    (go (while true
          (alt!
            nav-tap ([v] (nav-handler v history-imp))
            ;; Capture the current history for playback in the absence
            ;; of a server to store it
            (async/timeout 10000) (do #_(print "TODO: print out history: ")))))))


(defn ^:export setup! []
  (let [state (app-state)
        top-level-node (find-top-level-node)
        history-imp (history/new-history-imp top-level-node)]
    (set! debug-state state)
    (main state top-level-node history-imp)))

(setup!)
