spawning-grounds
================

memoizing caching for clojure, with timeout, prune, and purge options

caching functions can be defined using spawning-grounds.cache/make-cached
(see the files in src/spawning_grounds/cache/ for examples)

`(cache/make-cached f cached-form valid? tokenize reset-generate purge-argument reset-argument)`

* f -> the function whose results should be cached
* cached-form -> the function (usually identity) which generates the value to be compared
* valid? -> a function that compares the data stored alongside the cache to decide if the cached value should be used
* tokenize -> a function on the args to f, its results are stored alongside the cached values
* reset-generate -> a function which takes a reset argument, and generates a new valid? function
* purge-argument -> an argument that will purge the cache, with a key purges that key, with no key purges the whole cache
* reset-argument -> an argument that will reset the validation function

some premade caching functions are provided:

`(cache.timed/cached f timeout)`

* f -> the function whose results should be cached
* timeout -> the time in ms for which the cached results should be reused

    (def cfact
      (cache.timed/cached
        #(first
          (last
           (take-while
            (comp not zero? second)
            (iterate
             (fn [[acc i]] [(* acc i) (dec i)]) [1 %]))))
        (* 1000 60 60)))

cfact will compute the factorial of a number, and cache the value for an hour
before regenerating
