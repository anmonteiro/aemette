(ns {{name}}.routes
  (:require [cljs.core.async :as async]
            [secretary.core :as secretary :refer-macros [defroute]]))

(defn- send-nav! [nav-chan nav-target args]
  (async/put! nav-chan [nav-target args]))

(defn define-general-routes! [nav-chan]
  (defroute root-path "/" []
    (send-nav! nav-chan :landing {}))
  (defroute error-path "*" []
    (.log js/console "ERROR: NOT FOUND")
    (send-nav! nav-chan :error {:status 404})))

(defn define-routes! [state]
  (let [nav-ch (get-in @state [:comms :nav])]
    (define-general-routes! nav-ch)))
