(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [com.stuartsierra.component :as c]
            [stellar-cartography.system]))

(def system
  "A Var containing an object representing the application under
  development."
  nil)

(defn init
  "Creates and initializes the system under development in the Var
  #'system."
  []
  (alter-var-root #'system stellar-cartography.system/make-system))

(defn start
  "Starts the system running, updates the Var #'system."
  []
  (alter-var-root #'system c/start-system))

(defn stop
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (alter-var-root #'system c/stop-system))

(defn reset
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (refresh :after 'user/go))

(defn go
  "Initializes and starts the system running."
  []
  (init)
  (start)
  :ready)
