(ns tictac.engine)

(defn- x-cell? [x] (= "X" x))
(defn- o-cell? [o] (= "O" o))
(defn- full? [b] (not-any? nil? b))
(defn- ->index
  "Converts a [row col] to the board index.
  If given a cell index simply returns the index."
  [v]
  (if (sequential? v)
    (let [[row col] v] (+ col (* row 3)))
    v))
(defn- available-cells
  "Returns empty cell indices."
  [board]
  (keep-indexed #(when (nil? %2) %1) board))

(defn cell
  [board i]
  (board (->index i)))

(def empty-board [nil nil nil
                  nil nil nil
                  nil nil nil])

(defn turn
  "Returns whose turn it is given a board.
  \"X\", \"O\", or nil"
  [board]
  (let [xs      (count (filter x-cell? board))
        os      (count (filter o-cell? board))]
    (cond
      (full? board) nil
      (< os xs)     "O"
      :else         "X")))

(let [winners [[0 1 2] [3 4 5] [6 7 8]
               [0 3 6] [1 4 7] [2 5 8]
               [0 4 8] [2 4 6]]
      win? (fn [board combo] (let [[x y z] (map board combo)]
                               (when (= x y z) x)))]
  (defn winner?
    [board]
    (if-let [winner (some #(win? board %) winners)]
      winner
      (when (full? board) :tie))))

(defn move
  [board {:keys [player cell]}]
  (let [cell (->index cell)]
    (if (and (= player (turn board))
             (nil? (get board cell))
             (not (winner? board)))
      (assoc board cell player)
      board)))

(let [score (fn [maximizing winner]
              (get {maximizing ##Inf :tie 0} winner ##-Inf))]
  (def minimax
    "Finds next move via minimax for the current player of a tic-tac-toe game"
    (memoize  (fn
                ([board] (let [player (turn board)] (minimax board player player)))
                ([board player maximizing]
                 (if-let [winner (winner? board)]
                   {:score (score maximizing winner)}
                   (let [mfun  (if (= player maximizing) max-key min-key)
                         moves (for [cell (available-cells board)
                                     :let [m {:cell cell :player player}
                                           new-board (move board m)
                                           result (minimax new-board (turn new-board) maximizing)]]
                                 (assoc m :score (:score result)))]
                     (apply mfun :score moves))))))))

(comment
  (def board [nil nil nil
              nil nil nil
              nil nil nil])

  (-> board
      (move {:player "X" :cell [0 0]})
      (move {:player "O" :cell [1 1]})
      (move {:player "X" :cell [0 1]})
      (move {:player "O" :cell [1 2]}))

  (minimax board)

  ;; Recursively use minimax to play a tic-tac-toe game
  (loop [board board]
    (if-let [end (winner? board)]
      [board  end]
      (recur (move board (minimax board))))))
