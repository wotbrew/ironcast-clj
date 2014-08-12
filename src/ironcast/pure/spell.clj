(ns ironcast.pure.spell
  (:require [ironcast.util :refer :all]
            [ironcast.pure
             [act :refer :all]
             [pos :refer :all]
             [effect :refer :all]]))

(def types
  #{:tile
    :single
    :self
    :item})

(defn single?
  [spell]
  (= (:spell-type spell) :single))

(defn tile?
  [spell]
  (= (:spell-type spell) :tile))


;;MAGIC MISSILES

(def magic-missiles
  {:name :magic-missiles
   :text "Magic Missiles"
   :sprite :magic-missile
   :type :magic-missiles
   :spell-type :tile})

(defn magic-missiles-missile
  [from to caster n]
  (assoc (missile from to)
    :type :magic-missiles-strike
    :sprite :missile-magic-bolt
    :caster caster
    :spell magic-missiles
    :times n))

(defmethod applies? :magic-missiles
  [world caster pt _]
  (enemy-of-at? world caster pt))

(defmethod could? :magic-missiles
  [world caster pt _]
  true)

(defmethod aoe :magic-missiles
  [world caster [x y] spell]
  (square x y 3))

(defmethod prepare :magic-missiles
  [world caster pt spell]
  (let [aoe (aoe world caster pt spell)
        enemies (enemies-of-in-pts world caster aoe)
        freq (->> enemies cycle (take (count aoe)) frequencies)
        from (pos world caster)
        missiles (map (fn [[target n]]
                        (magic-missiles-missile from
                                                (pos world target)
                                                caster n)) freq)]
    (merge spell {:missiles missiles})))

(defmethod try-perform :magic-missiles
  [world spell]
  (success (reduce add-missile world (:missiles spell))))

(defmethod console-log :magic-missiles
  [world spell]
  "Cast Magic Missiles")

(defmethod console-log :magic-missiles-strike
  [world missile]
  (str-words "Magic Missiles hit" (:times missile) "times"))

;;SLEEP TOUCH

(def sleep-touch
  {:name :sleep-touch
   :text "Sleep Touch"
   :sprite :sleep
   :type :sleep-touch
   :spell-type :single})

(defmethod could? :sleep-touch
  [world caster pt _]
  (and
    (pos-adj? world caster pt)
    (enemy-of-at? world caster pt)))

(defmethod prepare :sleep-touch
  [world caster pt spell]
  spell)

(defmethod try-perform :sleep-touch
  [world spell]
  (success world))

(defmethod console-log :sleep-touch
  [world spell]
  "Cast Sleep Touch")
