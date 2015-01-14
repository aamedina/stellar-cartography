(ns stellar-cartography.galaxy.readers)

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
