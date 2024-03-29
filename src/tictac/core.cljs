(ns tictac.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [tictac.events :as events]
   [tictac.views :as views]
   [tictac.config :as config]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/router] root-el)))

(defn init []
  (when (= "localhost" js/location.hostname)
    (.useFunctionsEmulator (js/firebase.functions) "http://localhost:5001")
    (.settings (js/firebase.firestore) #js {:host "localhost:8080"
                                            :ssl false}))
  (re-frame/dispatch-sync [::events/initialize (random-uuid)])
  (dev-setup)
  (mount-root))
