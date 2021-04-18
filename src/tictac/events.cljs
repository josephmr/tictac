(ns tictac.events
  (:require
   [re-frame.core :as r]
   [tictac.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [tictac.engine :as e]))

;; Effect to storage provided key value into localStorage
;; e.g. {:local-storage {:key :foo :value "bar"}}
(r/reg-fx
 :local-storage
 (fn-traced [{:keys [key value]}]
            (.setItem js/localStorage key value)))

;; Effect to call Firebaes function with the event
(r/reg-fx
 :handle
 (fn-traced [event]
            (db/handle event)))

;; Effect to subscribe to a given document and store the doc at the provided
;; path in the db
(r/reg-fx
 :sub
 (fn-traced [{:keys [doc path]}]
            (db/subscribe
             doc
             (fn [data]
               (r/dispatch [::sub-changed path data])))))

(r/reg-event-db
 ::sub-changed
 (fn-traced [db [_ path data]]
            (assoc-in db path data)))

;; Coeffect to get :uuid from localStorage and assoc in cofx
(r/reg-cofx
 :uuid
 (fn-traced [cofx]
            (assoc cofx :uuid (.getItem js/localStorage :uuid))))

(r/reg-cofx
 :url-path
 (fn-traced [cofx]
            (let [path (subs (.-pathname js/window.location) 1)]
              (assoc cofx :url-path (when (seq path) path)))))

;; Initialize the db with base game state and store uuid in localStorage
(r/reg-event-fx
 ::initialize
 [(r/inject-cofx :uuid) (r/inject-cofx :url-path)]
 (fn-traced [{game-id :url-path uuid :uuid} [_ new-uuid]]
            {:db (if game-id
                   ;; eagerly set joining db as ::join dispatch is async
                   (db/join game-id)
                   db/default)
             :fx [[:local-storage {:key :uuid
                                   :value (or uuid new-uuid)}]
                  (when game-id [:dispatch [::join game-id]])]}))

(r/reg-event-fx
 ::join
 (fn-traced [_ [_ game-id]]
            {:db (db/join game-id)
             :fx [[:sub {:doc (str "games/" game-id) :path [:game]}]
                  [:handle {:event :join :game-id game-id}]]}))

(r/reg-event-fx
 ::play
 (fn-traced [_ [_ opponent]]
            (case opponent
              :computer {:db {:game {:board e/empty-board
                                     :vs :computer}
                              :page :game}})))

(r/reg-event-fx
 ::move
 (fn-traced [{:keys [db]} [_ cell]]
            (when-let [vs (get-in db [:game :vs])]
              (case vs
                :computer (let [board (get-in db [:game :board])
                                board (e/move board {:player "X" :cell cell})
                                board (e/move board (e/minimax board))]
                            {:db (assoc-in db [:game :board] board)})))))

(comment
  (r/dispatch-sync [::initialize (random-uuid)])
  (r/dispatch-sync [::move [0 2]]))
