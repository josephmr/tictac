(ns tictac.views
  (:require
   [re-frame.core :as re-frame]
   [tictac.subs :as subs]
   [tictac.events :as events]
   ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        set-name #(re-frame/dispatch [::events/update-name (-> % .-target .-value)])]
    [:div
     [:h1
      "Hello from " @name]
     [:input {:type "text"
              :on-change set-name}]
     ]))
