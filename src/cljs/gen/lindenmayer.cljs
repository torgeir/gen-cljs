(ns gen.lindenmayer)

;;   Given alphabet A.
;;
;;   `D0L Generate` takes three arguments:
;;   * An axiom  (a member of A)
;;   * A set of context-free rules over A (see description below)
;;   * A number indicating the number of rule applications.
;;
;;   `D1L Generate` takes three arguments:
;;   * An axiom  (a member of A)
;;   * A set of context-dependent rules over A (see description below)
;;   * A number indicating the number of rule applications.
;;
;;   A rule over an alphabet A is either:
;;   * Context-free: A 2-tuple consisting of input (a member of A) and output (a sequence over A).
;;   * Context-dependent: A 2-tuple consisting of an A-check and output (a sequence over A).
;;
;;   An A-check is a function that takes a sequence over A and an index, and returns a boolean value.


(defn applicable-rules
  "Find applicable rules for the unit at index of units."
  [units index rules]
  (->> rules
    (filter (fn [[pred fn]]
              (pred (units index))))
    (map second)))


(defn apply-rule
  "Apply `rule-fn` at location `index` of `units`."
  ([rule-fn units index]
   (-> index units rule-fn)))


(defn step
  "Run one step of rule applications on units.
  Called with 2 args, runs random applicable rule on each unit. Called with 3
  args, runs random applicable rule on index provided by `index-fn`. Will pull
  `index-fn` for new a new index until a rule can be applied for the unit at the
  provided index."
  ([units rules]
   (->> units
     (map-indexed (fn [index unit]
                    (let [rule-fns (applicable-rules units index rules)]
                      (if (empty? rule-fns)
                        unit
                        (apply-rule (rand-nth rule-fns) units index)))))
     (flatten)
     (vec)))
  ([units rules index-fn]
   (when-let [index (index-fn)]
     (let [rule-fns (applicable-rules units index rules)]
       (if (empty? rule-fns)
         (recur units rules index-fn)
         (->> (apply-rule (rand-nth rule-fns) units index)
           (assoc units index)
           (flatten)
           (vec)))))))


(defn rule-applier
  "Lazy seq of steps of applied rules to units."
  ([units rules] (rule-applier units rules step))
  ([units rules stepper]
   (lazy-seq
     (let [new-units (stepper units rules)]
       (cons new-units
             (rule-applier new-units rules stepper))))))