(ns tictac.db
  (:require [tictac.engine :as e]))

(def default-db
  {:board e/empty-board})
