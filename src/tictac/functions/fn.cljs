(ns tictac.functions.fn
  (:require    [tictac.engine :as e]
               [clojure.edn :as edn]
               ["firebase-functions" :as functions]
               ["firebase-admin" :as admin :refer [firestore]]))

(defonce init (.initializeApp admin))

(defmulti handle (fn [event] (:event event)))
(defmethod handle :default [_ _] nil)

(def db (.. admin firestore))

(defn coll
  [c]
  (. db (collection c)))

(defn doc
  [c id]
  (. (coll c) (doc id)))

(defn update!
  "Update a document based on its current value in a transaction."
  [docRef f]
  (.. db
      (runTransaction
       (fn [tx]
         (-> (.get tx docRef)
             (.then (fn [^js doc]
                      (when (.-exists doc)
                        (.update tx docRef (-> (.data doc)
                                               (js->clj :keywordize-keys true)
                                               f
                                               clj->js))))))))
      (then (fn [] nil))))

(defmethod handle :move
  [{:keys [game-id] :as data}]
  (update! (doc "games" game-id)
           (fn [doc]
             (update doc :board #(e/move % data)))))

(defmethod handle :create
  []
  (. (coll "games")
     (add (clj->js {:board e/empty-board}))))

(defn wrap-fn
  [handler]
  (fn
    [data context]
    (let [data (edn/read-string data)
          result (handler data context)]
      result)))

(def exports
  #js {:handle (->> handle
                    wrap-fn
                    (.onCall functions/https))})
