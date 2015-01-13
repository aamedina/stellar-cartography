(ns stellar-cartography.database
  (:require [datomic.api :as d]
            [com.stuartsierra.component :as c]
            [stellar-cartography.datomic :as db]
            [stellar-cartography.import :as import]))

(defrecord Database [uri conn]
  c/Lifecycle
  (start [this]
    (d/create-database uri)
    (if conn
      this
      (let [conn (d/connect uri)
            tx-data (db/resolve-schema)]
        (println :loading-schema)
        (d/transact conn tx-data)
        (assoc this :conn conn))))
  (stop [this]
    (if conn
      (do (d/release conn)
          (assoc this :conn nil))
      this))
  
  clojure.lang.IDeref
  (deref [_]
    (d/db conn))

  clojure.lang.IBlockingDeref
  (deref [_ timeout-ms timeout-val]
    (.deref (d/sync conn) timeout-ms timeout-val)))

(defmethod print-method Database
  [x writer]
  (.write writer (format "#<Database: %s>" (:uri x))))
