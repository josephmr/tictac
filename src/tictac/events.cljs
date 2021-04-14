(ns tictac.events
  (:require
   [re-frame.core :as re-frame :refer [path]]
   [tictac.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]

   [tictac.engine :as e]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-fx
 ::play
 (fn-traced [_ [_ opponent]]
            (case opponent
              :computer {:db {:game {:board e/empty-board
                                     :vs :computer}
                              :page :game}})))

(re-frame/reg-event-fx
 ::move
 (fn-traced [{:keys [db]} [_ cell]]
            (when-let [vs (get-in db [:game :vs])]
              (case vs
                :computer (let [board (get-in db [:game :board])
                                board (e/move board {:player "X" :cell cell})
                                board (e/move board (e/minimax board))]
                            {:db (assoc-in db [:game :board] board)})))))

(comment
  (re-frame/dispatch-sync [::initialize-db])
  (re-frame/dispatch-sync [::move [0 2]]))
