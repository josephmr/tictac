(ns tictac.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::page
 (fn [db]
   (:page db)))

(re-frame/reg-sub
 ::board
 (fn [db]
   (get-in db [:game :board])))
