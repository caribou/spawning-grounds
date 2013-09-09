(ns spawning-grounds.cache.file
  (:require [spawning-grounds.cache :as cache])
  (:import (java.io File)))

(defn filename-validator
  [[last-time filename]]
  (= (.lastModified (File. filename)) last-time))

(defn filename-tokenize
  [f [filename & _] & _]
  [(.lastModified (File. filename)) filename])

(defn name-cached
  "Given a function f taking a filename as its first argument, returns a caching
   function on a file that keeps older values until the file is changed, and
   accepts an optional purge argument to clear the cache."
  [f & [purge-argument]]
  (cache/make-cached
   f
   {:valid? filename-validator
    :tokenize filename-tokenize
    :reset-generate (constantly filename-validator)}))

(defn file-arg-compare
  "treat any two file args as equal if they have the same canonical file"
  [args]
  (cons (.getCanonicalFile (first args)) (rest args)))

(defn file-validator
  [[last-time file]]
  (= (.lastModified file) last-time))

(defn file-tokenize
  [f [file & _] & _]
  [(.lastModified file) file])

(defn file-cached
  "Given a fucntion f taking a file as its first argument, returns a caching
   function on a file that keeps older values until the file has changed, and
   accepts an option purge argument to clear the cache."
  [f & [purge-argument]]
  (cache/make-cached
   f
   {:cached-form file-arg-compare
    :valid? file-validator
    :tokenize file-tokenize
    :reset-generate (constantly file-validator)}))
