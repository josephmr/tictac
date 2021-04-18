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
