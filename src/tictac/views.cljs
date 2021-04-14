(ns tictac.views
  (:require
   [re-frame.core :as re-frame]
   [tictac.subs :as subs]
   [tictac.events :as events]
   [tictac.engine :as e]))

(defn cell [{:keys [value on-click]}]
  [:div
   {:class "w-32 h-32 border-2 border-white-600 rounded bg-white text-6xl
            flex justify-center items-center flex-col"
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
                                (re-frame/dispatch [::events/move [row col]]))}]))])

(defn button
  [{:keys [on-click]} text]
  [:button {:class "py-2 px-4 bg-red-500 text-white font-semibold rounded-lg
                    shadow-md focus:outline-none focus:ring-2
                    focus:ring-purple-600 focus:border-transparent
                    focus:ring-offset-2 focus:ring-offset-purple-200
                    active:bg-red-800"
            :on-click on-click}
   text])

(defn start
  []
  [:div {:class "flex flex-col container mx-auto items-center justify-center h-screen"}
   [:div {:class "flex flex-col bg-red-200 p-10 w-1/3 h-1/3 rounded-lg justify-between"}
    [:h1 {:class "text-6xl text-purple-600 text-center font-bold"} "Tic Tac"]
    [:div {:class "flex flex-col space-y-4"}
     [button
      {}
      "vs. Player"]
     [button
      {:on-click #(re-frame/dispatch [::events/play :computer])}
      "vs. AI"]]]])

(defn game []
  (let [board-state @(re-frame/subscribe [::subs/board])
        winner (e/winner? board-state)]
    [:div {:class "flex flex-col container mx-auto items-center justify-center h-screen"}
     (when winner [:h1 winner])
     [board board-state]]))

(defn router []
  (let [page @(re-frame/subscribe [::subs/page])]
    (case page
      :start [start]
      :game [game])))
