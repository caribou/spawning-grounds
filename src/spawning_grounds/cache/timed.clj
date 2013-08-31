(ns spawning-grounds.cache.timed
  (:require [spawning-grounds.cache :as cache])
  (:import (java.util Date)))

(def stamp
 "Creates a numeric time stamp representing right now."
  #(.getTime (Date.)))


(defn make-time-validator
  "Generate a validator that ensures a cached item is not too old."
  [ms]
  (fn [time]
    (> ms (- (stamp) time))))

(defn time-tokenize
  [& args]
  "Creates a token that will mark a cached item, so we can track its age"
  (stamp))

(defn cached
  "Given a function f, returns a caching function that keeps older values
   for a fixed period of time (in milliseconds), and accepts optional purge
   and timeout-reset arguments for the cached data."
  [f timeout & [caching-compare purge-argument reset-argument]]
  (cache/make-cached
   f
   (or caching-compare identity)
   (make-time-validator timeout)
   time-tokenize
   make-time-validator
   (or purge-argument :purge)
   (or reset-argument :reset)))
