(ns spawning-grounds.cache)

(defn prune!
  "Removes a key from the store. with no key argument, purges the store."
  ([store]
     (reset! store {}))
  ([store & key]
     (swap! store dissoc key)))

(defn validation!
  "Changes the validation function for the store."
  ([state reset-generate]
     (deref state))
  ([state value reset-generate]
     (reset! state (reset-generate value))))

(defn lookup!
  "Attempts to lookup a valid value from the stored state. If not found or not
   valid, it generates a result and stores it."
  [state f cached-form args valid? tokenize]
  (let [[token cached] (get @state (cached-form args))]
    (if (or (nil? cached)
            (not (valid? token)))
      (let [new-result (apply f args)]
        (swap! state assoc (cached-form args) [(tokenize f args) new-result])
        new-result)
      cached)))

(defn make-cached
  [f {:keys [cached-form
             valid?
             tokenize
             reset-generate
             purge-argument
             reset-argument]
      :or {cached-form identity
           valid? (constantly true)
           tokenize (constantly true)
           reset-generate identity
           purge-argument :purge
           reset-argument :reset}}]
  (let [store (atom {})
        validator (atom valid?)]
    (fn [& [action? & rest :as args]]
      (cond (= action? purge-argument) (apply prune! store rest)
            (= action? reset-argument) (apply validation! validator rest
                                              reset-generate)
            :default (lookup! store f cached-form args @validator tokenize)))))
