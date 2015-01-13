(ns stellar-cartography.datomic
  (:require [datomic.api :as d]
            [clojure.string :as str]
            [clojure.tools.namespace.find :as find]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [clojure.java.io :as io]))

(clojure.tools.namespace.repl/disable-reload!)

(definterface ITransactableEntity
  (attributes [])
  (metaAttributes [])
  (asTransactable [])
  (asEntity [db])
  (syncEntity [db]))

(def empty-attribute
  {:db/id nil
   :db/ident nil
   :db/valueType nil
   :db/cardinality :db.cardinality/one
   :db/doc nil
   :db/unique nil
   :db/index false
   :db/fulltext false
   :db/isComponent nil
   :db/noHistory false})

(defn db-attribute
  [entity-name field]
  (let [m (meta field)
        ident (or (:db/ident m) (keyword entity-name (name field)))]
    (assert (or (:db/valueType m) (:tag m))
            (format "%s missing db.type annotation" (str ident)))
    (into {} (filter val)
          (merge empty-attribute
                 {:db/id (d/tempid :db.part/db)
                  :db/ident ident
                  :db/valueType (keyword "db.type" (name (:tag m)))
                  :db.install/_attribute :db.part/db}
                 (select-keys m (keys empty-attribute))))))

(defn entity-fn
  [entity-name [ident [params code]] {:keys [requires imports]
                                      :or {requires [] imports []}}]
  {:db/id (d/tempid :db.part/user)
   :db/ident (if (namespace ident)
               ident
               (keyword entity-name (name ident)))
   :db/doc ""
   :db/fn (d/function {:lang "clojure"
                       :params params
                       :requires requires
                       :imports imports
                       :code code})})

(defn enum
  ([ident] (enum (keyword (first (str/split (namespace ident) #"\."))) ident))
  ([part ident]
   {:db/id (d/tempid part)
    :db/ident ident}))

(defmacro defentity
  [entity-name fields & opts+specs]
  (let [[opts specs] (loop [[k v & specs :as full-specs] opts+specs
                            opts {}]
                       (if (keyword? k)
                         (recur specs (assoc opts k v))
                         [opts full-specs]))
        partition (or (:partition opts) (keyword (ns-name *ns*)))
        partition-entity (when-not (:partition opts)
                           {:db/id (d/tempid :db.part/db)
                            :db/ident partition
                            :db.install/_partition :db.part/db})
        fn-opts (select-keys opts [:requires :imports])
        entity-str (name entity-name)
        db-fns (mapv #(entity-fn entity-str % fn-opts)
                     (dissoc opts :requires :imports))
        attributes (mapv #(db-attribute entity-str %) fields)
        bindings (conj (mapv #(with-meta % {}) fields) 'eid)
        attr-map (group-by (comp symbol name :db/ident) attributes)]
    `(do (defrecord ~entity-name ~bindings
           ITransactableEntity
           (~'attributes [this#]
             (read-string ~(pr-str (into (if partition-entity
                                           [partition-entity]
                                           [])
                                         (into attributes db-fns)))))
           (~'metaAttributes [this#]
             ~(let [ks (keys empty-attribute)]
                (zipmap (map :db/ident attributes)
                        (map #(apply dissoc (meta %) :tag ks) fields))))
           (~'asTransactable [this#]
             (let [m# ~(into {:db/id `(d/tempid ~partition)}
                             (map (fn [field]
                                    (let [attrs (first (get attr-map field))
                                          e? (identical? (:db/valueType attrs)
                                                         :db.type/ref)
                                          b (with-meta field {})]
                                      [(:db/ident attrs)
                                       (if e?
                                         `(when ~b
                                            (cond
                                              (or (vector? ~b)
                                                  (set? ~b)
                                                  (sequential? ~b))
                                              (mapv #(cond-> %
                                                       (keyword? %)
                                                       (->> (enum ~partition))
                                                       (not (keyword? %))
                                                       (-> .asTransactable
                                                           (dissoc :db/id))) ~b)
                                              (keyword? ~b) (enum ~partition ~b)
                                              :else (dissoc (.asTransactable ~b)
                                                            :db/id)))
                                           b)])))
                             fields)]
               (into {} (remove (fn [[k# v#]]
                                  (cond
                                    (vector? v#) (empty? v#)
                                    (map? v#) (== (count v#) 1)
                                    :else (nil? v#))))
                     (if ~'eid
                       (.assoc m# :db/id ~'eid)
                       m#))))
           (~'asEntity [this# db#]
             (assert ~'eid "Cannot reify entities without an id")
             (d/touch (d/entity db# ~'eid)))
           (~'syncEntity [this# db#]
             (let [entity# (.asEntity this# db#)]
               (reduce (fn [m# [k# v#]]
                         (.assoc m# (keyword (name k#)) v#))
                       this# entity#)))
           ~@specs))))

(defn ns-pattern
  [ns]
  (re-pattern (str (first (str/split (name (ns-name ns)) #"\.")) ".*")))

(defonce namespaces (find/find-namespaces [(io/file "src")]))

(defn load-namespaces
  []
  (letfn [(find-namespaces [oldval]
            (refresh)
            (let [files [(io/file "src")]]
              (find/find-namespaces files)))]
    (alter-var-root #'namespaces find-namespaces)))

(defn compile-ns-matcher
  ([] (compile-ns-matcher namespaces))
  ([libs] (re-pattern (str/join \| (map #(str "^" % ".*") libs)))))

(defn resolve-schema
  [& {:keys [libs]}]
  (let [libs (or libs (load-namespaces))
        re (compile-ns-matcher libs)]
    (into [] (comp (map find-ns)
                   (mapcat (comp vals ns-publics))
                   (map (fn [v]
                          (some->>
                           (name (.-sym v))
                           (re-find #"^map->(.*)")
                           (peek)
                           (symbol)
                           (ns-resolve (.-ns v)))))
                   (filter class?)
                   (filter #(.isAssignableFrom ITransactableEntity %))
                   (filter #(.isAssignableFrom clojure.lang.IRecord %))
                   (map #(read-string (str "#" (.getName %) "{}")))
                   (mapcat #(.attributes %)))
          libs)))
