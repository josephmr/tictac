(ns tictac.db)

(defn handle
  "Call Firebase function with given event.
  For example, {:event :move :game-id \"foo\" :player \"X\" :cell [1 2]}."
  [event]
  (let [f  (.. js/firebase
               functions
               (httpsCallable "handle"))]
    (->> event
         pr-str
         f)))

(comment
  (tictac.db/handle {:event :create})
  (tictac.db/handle {:event :join
                     :game-id "aRHjUbnthalNojLsl696"
                     :uuid "player-1"})
  (tictac.db/handle {:event :move
                     :game-id "NETkLgB6W00CKrICFPcB"
                     :player "X"
                     :cell [1 1]}))

(def default
  {:page :start})

(defn join
  [url-path]
  {:page :join
   :game-id url-path})

(defonce subscriptions (atom {}))

(defn subscribe
  "Subscribes to Firestore document and calls callback with document data for
  each change.

  Only one subscription is allowed per document. Attempting to subscribe again
  to the same document will have no effect. First unsubscribe, then subscribe
  again if necessary."
  [ref f]
  (when (not (@subscriptions ref))
    (let [unsub
          (.. js/firebase
              firestore
              (doc ref)
              (onSnapshot (fn [snapshot] (f (js->clj (.data snapshot)
                                                     :keywordize-keys true)))))]
      (swap! subscriptions assoc ref unsub))))

(defn unsubscribe
  "Unsubscribes from a given Firestore document."
  [ref]
  (when-let [unsub (@subscriptions ref)]
    (unsub)
    (swap! subscriptions dissoc ref)))

(comment (subscribe "games/foo" #(println %))
         @subscriptions
         (unsubscribe "games/foo"))
