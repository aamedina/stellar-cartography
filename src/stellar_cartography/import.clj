(ns stellar-cartography.import
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [clojure.data.csv :as csv]
            [clojure.string :as str]
            [clojure.core.async :as a :refer [pipeline to-chan chan <!!]]
            [environ.core :refer [env]]
            [datomic.api :as d :refer [q]]
            [stellar-cartography.galaxy :as galaxy]))

(defn slurp-gz
  [path]
  (with-open [in (java.util.zip.GZIPInputStream. (io/input-stream path))]
    (slurp in)))

(defn ensure-resource
  [[uri path]]
  (some-> (or (some-> (io/resource path) slurp)
              (let [content (if (.endsWith uri "gz")
                              (slurp-gz uri)
                              (slurp uri))]
                (spit (str "resources/" path) content)
                content))
          csv/read-csv))

(defonce datasets
  (delay {:exoplanets (ensure-resource (env :exoplanets))
          :stars (ensure-resource (env :stars))
          :deep-sky-objects (ensure-resource (env :deep-sky-objects))}))

(defonce num-cpus (.availableProcessors (Runtime/getRuntime)))

(defn row-reader
  [fs]
  (->> fs
       (map-indexed (fn [n f]
                      #(when-not (or (str/blank? (nth % n))
                                     (= "NULL" (nth % n)))
                         (try
                           (cond
                             (= "inf" (nth % n)) Float/POSITIVE_INFINITY
                             (= "nan" (nth % n)) Float/NaN
                             :else (f (nth % n)))
                           (catch Throwable t
                             (println (nth % n) n %)
                             (throw t))))))
       (apply juxt)))

(def dataset-xf
  (mapcat (fn [[k v]]
            (let [ctor (case k
                         :exoplanets galaxy/->planet
                         :stars galaxy/->star
                         :deep-sky-objects galaxy/->deep-sky-object)
                  rf (case k
                       :exoplanets (row-reader galaxy/planet-readers)
                       :stars (row-reader galaxy/star-readers)
                       :deep-sky-objects (row-reader galaxy/deep-sky-readers))]
              (into [] (comp (map rf)
                             (map #(apply ctor (conj % nil)))
                             (map #(.asTransactable %))) (next v))))))

(defn import-datasets
  [conn]
  (let [to (chan 256 (comp (partition-all 1000)
                           (map #(d/transact-async conn %))
                           (partition-all 20)
                           (mapcat #(map deref %))))
        from (to-chan @datasets)
        ex-handler (fn [ex]
                     (.printStackTrace ex)
                     (throw ex))
        _ (pipeline num-cpus to dataset-xf from true ex-handler)]
    (while (<!! to))))
