(ns tictac.events
  (:require
   [re-frame.core :as re-frame]
   [tictac.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [tictac.engine :as e]))

;; Effect to storage provided key value into localStorage
;; e.g. {:local-storage :foo "bar"}
(re-frame/reg-fx
 :local-storage
 (fn-traced [[key value]]
            (.setItem js/localStorage key value)))

;; Coeffect to get :uuid from localStorage and assoc in cofx
(re-frame/reg-cofx
 :uuid
 (fn-traced [cofx]
            (assoc cofx :uuid (.getItem js/localStorage :uuid))))

(re-frame/reg-cofx
 :url-path
 (fn-traced [cofx]
            (let [path (subs (.-pathname js/window.location) 1)]
              (assoc cofx :url-path (when (seq path) path)))))

;; Initialize the db with base game state and store uuid in localStorage
(re-frame/reg-event-fx
 ::initialize
 [(re-frame/inject-cofx :uuid) (re-frame/inject-cofx :url-path)]
 (fn-traced [{:keys [uuid url-path]} [_ new-uuid]]
            {:db (if url-path
                   (db/join url-path)
                   db/default)
             :local-storage [:uuid (or uuid new-uuid)]}))

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
  (re-frame/dispatch-sync [::initialize (random-uuid)])
  (re-frame/dispatch-sync [::move [0 2]]))
