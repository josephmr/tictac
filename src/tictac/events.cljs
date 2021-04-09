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


(re-frame/reg-event-db
  ::update-cell
  (path [:board])
  (fn-traced [board [_ cell]]
             (e/move board {:cell cell})))
