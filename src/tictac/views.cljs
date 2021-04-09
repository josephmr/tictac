(ns tictac.views
  (:require
   [re-frame.core :as re-frame]
   [tictac.subs :as subs]
   [tictac.events :as events]
   [tictac.engine :as e]))

(defn cell [{:keys [value on-click]}]
  [:div
   {:class "w-32 h-32 border-2 border-white-600 rounded bg-white"
    :on-click #(when on-click (on-click))}
   (str value)])

(defn board [board]
  [:div {:class "grid grid-cols-3 grid-rows-3 gap-2 p-2 bg-red-100 rounded"}
   (doall (for [row (range 3)
                col (range 3)
                :let [value (e/cell board [row col])]]
            ^{:key [col row]}
            [cell {:value value
                   :on-click #(when (nil? value)
                                (re-frame/dispatch [::events/update-cell [row col]]))}]))])

(defn main-panel []
  (let [board-state @(re-frame/subscribe [::subs/board])
        winner (e/winner? board-state)]
    [:div {:class "flex flex-col container mx-auto items-center justify-center h-screen"}
     (when winner [:h1 winner])
     [board board-state]]))

