(ns tictac.views
  (:require
   [re-frame.core :as re-frame]
   [tictac.subs :as subs]
   [tictac.events :as events]
   ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        set-name #(re-frame/dispatch [::events/update-name (-> % .-target .-value)])]
    [:div {:class "flex flex-col container mx-auto items-center justify-center h-screen"}
     [:h1 {:class "text-blue-700 text-3xl p-10"}
      "Hello from " @name]
     [:input {:class "border-2 rounded focus:ring-2 focus:ring-blue-300 w-44"
              :type "text"
              :on-change set-name}]]))

