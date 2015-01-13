(ns stellar-cartography.system
  (:require [com.stuartsierra.component :as c]
            [environ.core :refer [env]]
            [stellar-cartography.database :as db]))

(defn make-system
  [_]
  (c/system-map
   :db (db/map->Database (env :datomic))))
