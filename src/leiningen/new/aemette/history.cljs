(ns {{name}}.history
  (:require [clojure.string :as string]
            [goog.events :as events]
            [goog.history.Html5History :as html5-history]
            [secretary.core :as secretary]))

(defn route-fragment
  "Returns the route fragment if this is a route that we've don't dispatch
  on fragments for."
  [path]
  (-> path
      secretary/locate-route
      :params
      :_fragment))


(defn path-matches?
  "True if the two tokens are the same except for the fragment"
  [token-a token-b]
  (= (first (string/split token-a #"#"))
     (first (string/split token-b #"#"))))


(defn new-window-click? [event]
  "It's a new window click either when the mouse middle button
  is clicked, or when the left mouse button is clicked while
  pressing the modifier key (Ctrl in most systems)"
  (or (.isButton event goog.events.BrowserEvent.MouseButton.MIDDLE)
      (and (.-platformModifierKey event)
           (.isButton event goog.events.BrowserEvent.MouseButton.LEFT))))


(defn setup-link-dispatcher! [history-imp top-level-node]
  (let [dom-helper (goog.dom.DomHelper.)]
    (events/listen top-level-node "click"
                   #(let [-target (.-target %)
                          target (if (= (.-tagName -target) "A")
                                   -target
                                   (.getAncestorByTagNameAndClass dom-helper -target "A"))
                          location (when target (str (.-pathname target) (.-search target) (.-hash target)))
                          new-token (when (seq location) (subs location 1))]
                      (when (and (seq location)
                                 (= (.. js/window -location -hostname)
                                    (.-hostname target))
                                 (not (or (new-window-click? %) (= (.-target target) "_blank"))))
                        (.preventDefault %)
                        (if (and (route-fragment location)
                                 (path-matches? (.getToken history-imp) new-token))

                          (do (.log js/console "scrolling to hash for" location)
                              ;; don't break the back button
                              (.replaceToken history-imp new-token))

                          (do (.log js/console "navigating to" location)
                              (.setToken history-imp new-token))))))))


(defn set-current-token!
  "Lets us keep track of the history state, so that we don't dispatch twice on the same URL"
  [history-imp & [token]]
  (set! (.-_current_token history-imp) (or token (.getToken history-imp))))

(defn disable-erroneous-popstate!
  "Stops the browser's popstate from triggering NAVIGATION events unless the url has really
   changed."
  [history-imp]
  (let [window (.-window_ history-imp)]
    (events/removeAll window goog.events.EventType.POPSTATE)
    (events/listen window goog.events.EventType.POPSTATE
                   #(if (= (.getToken history-imp)
                           (.-_current_token history-imp))
                      (js/console.log "Ignoring duplicate dispatch event to" (.getToken history-imp))
                      (.onHistoryEvent_ history-imp)))))


(defn setup-dispatcher!
  "We might want to ignore the first event dispatched by the Html5History lib,
  because it will make a route dispatch"
  ([history-imp] (setup-dispatcher! history-imp false))
  ([history-imp ignore-first?]
    (if ignore-first?
      (events/listenOnce history-imp goog.history.EventType.NAVIGATE #(setup-dispatcher! history-imp))
      (events/listen history-imp goog.history.EventType.NAVIGATE
                     #(do (set-current-token! history-imp)
                          (secretary/dispatch! (str "/" (.-token %))))))))


(defn new-history-imp [top-level-node]
  ;; need a history element, or goog will overwrite the entire dom
  ;; not sure if it's needed
  (comment (let [dom-helper (goog.dom.DomHelper.)
        node (.createDom dom-helper "input" #js {:class "history hide"})]
    (.append dom-helper node)))
  (doto (goog.history.Html5History. js/window)
    (.setUseFragment false)
    (.setPathPrefix "/")
    (setup-dispatcher!)
    (set-current-token!) ; Stop Safari from double-dispatching
    (disable-erroneous-popstate!) ; Stop Safari from double-dispatching
    (.setEnabled true) ; This will fire a navigate event with the current token
    (setup-link-dispatcher! top-level-node)))
