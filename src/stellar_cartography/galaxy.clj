(ns stellar-cartography.galaxy
  (:require [datomic.api :as d :refer [q]]
            [stellar-cartography.datomic :as db :refer [defentity]]))

(defentity deep-sky-object [^float ra
                            ^float dec
                            ^string ^:db/index type
                            ^string ^:db/fulltext ^:db/index const
                            ^float mag
                            ^string ^:db/fulltext ^:db/index name
                            ^float rarad
                            ^float decrad
                            ^long ^{:db/unique :db.unique/identity} id
                            ^float r1
                            ^float r2
                            ^float angle
                            ^string ^:db/fulltext dso-source
                            ^string ^:db/fulltext id1
                            ^string ^:db/fulltext cat1
                            ^string ^:db/fulltext id2
                            ^string ^:db/fulltext cat2
                            ^string ^:db/fulltext dupid
                            ^string ^:db/fulltext dupcat
                            ^string display-mag])


(def deep-sky-readers
  [#(Float/parseFloat %)
   #(Float/parseFloat %)
   identity
   identity
   #(Float/parseFloat %)
   identity
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Long/parseLong %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   identity
   identity
   identity
   identity
   identity
   identity
   identity
   identity])

(defentity planet [^string ^:db/fulltext ^{:db/unique :db.unique/identity} name
                   ^float ^:db/index mass
                   ^float mass-error-min
                   ^float mass-error-max
                   ^float ^:db/index radius
                   ^float radius-error-min
                   ^float radius-error-max
                   ^float ^:db/index orbital-period
                   ^float orbital-period-err-min
                   ^float orbital-period-err-max
                   ^float ^:db/index semi-major-axis
                   ^float semi-major-axis-error-min
                   ^float semi-major-axis-error-max
                   ^float ^:db/index eccentricity
                   ^float eccentricity-error-min
                   ^float eccentricity-error-max
                   ^float ^:db/index angular-distance
                   ^float ^:db/index inclination
                   ^float inclination-error-min
                   ^float inclination-error-max
                   ^float ^:db/index tzero-tr
                   ^float tzero-tr-error-min
                   ^float tzero-tr-error-max
                   ^float ^:db/index tzero-tr-sec
                   ^float tzero-tr-sec-error-min
                   ^float tzero-tr-sec-error-max
                   ^float ^:db/index lambda-angle
                   ^float lambda-angle-error-min
                   ^float lambda-angle-error-max
                   ^float ^:db/index impact-parameter
                   ^float impact-parameter-error-min
                   ^float impact-parameter-error-max
                   ^float ^:db/index tzero-vr
                   ^float tzero-vr-error-min
                   ^float tzero-vr-error-max
                   ^float ^:db/index K
                   ^float K-error-min
                   ^float K-error-max
                   ^float temp-calculated
                   ^float temp-measured
                   ^float hot-point-lon
                   ^float ^:db/index albedo
                   ^float albedo-error-min
                   ^float albedo-error-max
                   ^float log-g
                   ^float publication-status
                   ^instant ^:db/index discovered
                   ^instant ^:db/index updated
                   ^float ^:db/index omega
                   ^float omega-error-min
                   ^float omega-error-max
                   ^float ^:db/index tperi
                   ^float tperi-error-min
                   ^float tperi-error-max
                   ^string ^:db/fulltext detection-type
                   ^string ^:db/fulltext mass-detection-type
                   ^string ^:db/fulltext radius-detection-type
                   ^string ^:db/fulltext alternate-names
                   ^string ^:db/fulltext molecules
                   ^string ^:db/fulltext star-name
                   ^float ra
                   ^float dec
                   ^float mag-v
                   ^float mag-i
                   ^float mag-j
                   ^float mag-h
                   ^float mag-k
                   ^float star-distance
                   ^float star-metallicity
                   ^float star-mass
                   ^float star-radius
                   ^string ^:db/fulltext star-sp-type
                   ^float ^:db/index star-age
                   ^float star-teff
                   ^float star-detected-disc
                   ^float star-magnetic-field])

(defn read-date
  [date]
  (when date
    (java.util.Date. (.replace date \- \/))))

(defn read-year
  [year]
  (when year
    (read-date (str "01/01/" year))))

(def planet-readers
  [identity
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %)
   read-year
   read-date
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %)
   identity
   identity
   identity
   identity
   identity
   identity
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %)
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   identity
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %)])

(defentity star [^long ^{:db/unique :db.unique/identity} id
                 ^long ^:db/index hip
                 ^long ^:db/index hd
                 ^long ^:db/index hr
                 ^string ^:db/fulltext ^:db/index gl
                 ^string ^:db/fulltext ^:db/index bf
                 ^string ^:db/fulltext ^:db/index name
                 ^float ^:db/index right-ascension
                 ^float ^:db/index declination
                 ^float ^:db/index distance
                 ^float proper-motion-right-ascension
                 ^float proper-motion-declination
                 ^float radial-velocity
                 ^float ^:db/index magnitude
                 ^float absolute-magnitude
                 ^string ^:db/fulltext ^:db/index spectral-type
                 ^float ^:db/index color-index
                 ^float ^:db/index x
                 ^float ^:db/index y
                 ^float ^:db/index z
                 ^float vx
                 ^float vy
                 ^float vz
                 ^float rarad
                 ^float decrad
                 ^float pmrarad
                 ^float pmdecrad
                 ^string bayer
                 ^long flam
                 ^string ^:db/fulltext con
                 ^long component
                 ^long component-primary
                 ^string ^:db/fulltext base
                 ^double ^:db/index luminosity
                 ^string var
                 ^float var-min
                 ^float var-max])
(def star-readers
  [#(Long/parseLong %)
   #(Long/parseLong %)
   #(Long/parseLong %)
   #(Long/parseLong %)
   identity
   identity
   identity
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   identity
   #(Float/parseFloat %) 
   #(Float/parseFloat %) 
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   #(Float/parseFloat %)
   identity
   #(Long/parseLong %)
   identity
   #(Long/parseLong %)
   #(Long/parseLong %)
   identity
   #(Double/parseDouble %)
   identity
   #(Float/parseFloat %)
   #(Float/parseFloat %)])
