(defproject stellar-cartography "0.1.0-SNAPSHOT"
  :description ""
  :url ""
  :license {:name "GNU General Public License"
            :url "http://www.gnu.org/licenses/gpl.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha5"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/data.csv "0.1.2"]
                 [com.datomic/datomic-pro "0.9.5130" :exclusions [joda-time]]
                 [com.stuartsierra/component "0.2.2"]
                 [environ "1.0.0"]]
  :plugins [[lein-environ "1.0.0"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.8"]]
                   :env {:datomic {:uri ~(str "datomic:dev://localhost:4334/"
                                              "stellar-cartography")}}
                   :source-paths ["dev"]}}
  :repositories {"sonatype" "https://oss.sonatype.org/content/groups/public/"
                 "my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :env {:exoplanets ["http://exoplanet.eu/catalog/csv/" "exoplanets.csv"]
        :stars [~(str "https://raw.githubusercontent.com/astronexus"
                      "/HYG-Database/master/hygdata_v3.csv.gz") "stars.csv"]
        :deep-sky-objects [~(str "https://raw.githubusercontent.com/astronexus"
                                 "/HYG-Database/master/dso.csv") "dso.csv"]}
  :jvm-opts ^:replace ["-server"
                       "-XX:+UseConcMarkSweepGC"
                       "-Xmx4g"])
