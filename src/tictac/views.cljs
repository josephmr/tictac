(ns tictac.views
  (:require
   [re-frame.core :as re-frame]
   [tictac.subs :as subs]
   [tictac.events :as events]
   [tictac.engine :as e]))

(defn cell [{:keys [value on-click]}]
  [:div
   {:class (str "w-32 h-32 rounded-lg bg-gray-50 text-6xl
                  flex justify-center items-center flex-col
                  text-gray-700 "
                (if on-click
                  "hover:bg-gray-200 cursor-pointer"
                  "cursor-default"))
    :on-click #(when on-click (on-click))}
   (str value)])

(defn board [board]
  [:div {:class "grid grid-cols-3 grid-rows-3 gap-3 p-3 bg-green-300 rounded-lg"}
   (doall (for [row (range 3)
                col (range 3)
                :let [value (e/cell board [row col])]]
            ^{:key [col row]}
            [cell {:value value
                   :on-click (when (and (nil? value)
                                        (not (e/winner? board)))
                               #(re-frame/dispatch [::events/move [row col]]))}]))])

(defn button
  [{:keys [on-click]} text]
  [:button {:class "py-2 px-4 bg-red-500 text-white font-semibold rounded-lg
                    focus:outline-none hover:bg-red-600 hover:ring-2
                    active:bg-red-500"
            :on-click on-click}
   text])

(defn start
  []
  [:div {:class "relative"}
   [board e/empty-board]
   [:div {:class "absolute inset-0 flex flex-col w-full h-full p-8 space-y-4 justify-center"}
    [button {} "vs Player"]
    [button {:on-click #(re-frame/dispatch [::events/play :computer])}
     "vs AI"]]])

(defn game []
  (let [board-state @(re-frame/subscribe [::subs/board])
        winner (e/winner? board-state)]
    (when winner [:h1 winner])
    [board board-state]))

(defn scaffold [children]
  [:div {:class "flex flex-col container mx-auto items-center"}
   [:h1 {:class "font-bold text-6xl py-8 text-gray-700"}
    "Tic Tac"]
   children])

(defn router []
  (let [page @(re-frame/subscribe [::subs/page])]
    (case page
      :join [scaffold [:h1 "Joining"]]
      :start [scaffold [start]]
      :game [scaffold [game]])))
