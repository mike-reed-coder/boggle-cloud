(ns boggle.boggle-test
  (:require [midje.sweet :refer :all]
            [boggle.boggle :as boggle]))

(def board2 ["ab" "cd"])
(def board3 ["abc" "def" "ghi"])
(def board4 ["abcd" "efgh" "ijkl" "mnop"])

(facts "valid-board?"
  (tabular
    (fact "can validate board"
      (boggle/valid-board? ?board) => ?exp)
    ?board                        ?exp
    board2                        truthy
    board3                        truthy
    board4                        truthy
    [["a" "b"] ["c" "d"]]         falsey
    []                            falsey
    ["ab" "c"]                    falsey
    ["abcd" "egh" "hijk" "lmno"]  falsey))

(facts "is-word?"
  (tabular
    (fact "sanity check our dictionary"
      (boggle/is-word? ?word) => ?exp)
    ?word         ?exp
    "run"         true
    "running"     true
    "runs"        true
    "ran"         true
    "rune"        true
    "runes"       true
    "lskdjf"      falsey
    "boglgle"     falsey))

(facts "get-2d"
  (tabular
    (fact "can get a char at 2d coords: x y"
      (boggle/get-2d board2 ?x ?y) => ?exp)
    ?x  ?y  ?exp
    0   0   \a
    1   0   \b
    0   1   \c
    1   1   \d))

(facts "assoc-2d"
  (tabular
    (fact "can assign a val at 2d coords: x y"
      (boggle/assoc-2d board2 ?x ?y 0) => ?exp)
    ?x  ?y  ?exp
    0   0   ["0b" "cd"]
    1   0   ["a0" "cd"]
    0   1   ["ab" "0d"]
    1   1   ["ab" "c0"]))

(facts "neighbors"
  (tabular
    (fact "can find all unmarked neighbors"
      (boggle/neighbors ?board ?x ?y) => ?exp)
    ?board          ?x  ?y  ?exp
    ["ab" "cd"]     0   0   [[0 1] [1 0] [1 1]]
    ["ab" "cd"]     1   0   [[0 0] [0 1] [1 1]]
    ["a0" "cd"]     0   0   [[0 1] [1 1]]
    ["a0" "c0"]     0   0   [[0 1]]
    ["a0" "00"]     0   0   []
    board3          1   1   [[0 0] [0 1] [0 2] [1 0] [1 2] [2 0] [2 1] [2 2]]))

(facts "depth-find"
  (tabular
    (fact "can find all words from a starting point"
      (boggle/depth-find ?board ?x ?y) => ?exp)
    ?board          ?x          ?y        ?exp
    ["st" "ar"]     0           0         ["sat" "star"]
    ["st" "ar"]     1           0         ["tsar" "tas" "tars" "tar"]
    ["st" "ar"]     0           1         ["ars" "arts" "art"]
    ["st" "ar"]     1           1         ["ras" "rats" "rat"]))

(facts "find-all-words"
  (tabular
    (fact "can find all words in a boggle board"
      (boggle/find-all-words ?board) => ?exp)
    ; 4x4 takes too long for tests :(
    ?board                          ?exp
    ["st" "ar"]                     #{"ars" "art" "ras" "rat" "tar" "tas" "sat" "rats" "tars" "arts" "tsar" "star"}
    ["sta" "rwa" "rse"]             #{"awes" "sew" "seat" "tas" "sat" "twae" "taws" "wats" "was" "saw" "saws" "wat"
                                      "taw" "awa" "twaes" "sews" "seats" "sea" "awe" "aas" "sweat" "twas" "swat"
                                      "staw" "sae" "waes" "eat" "tae" "eats" "wae" "swats" "tawse" "twa" "sweats"}))

(facts "score"
  (tabular
    (fact "can count score for word"
      (boggle/score ?word) => ?exp)
    ?word               ?exp
    ""                  0
    "a"                 0
    "ab"                0
    "abc"               1
    "abcd"              1
    "abcde"             2
    "abcdef"            3
    "abcdefg"           5
    "abcdefgh"          11
    "abcdefghi"         11))

(facts "score-words"
  (tabular
    (fact "can count a list of words"
      (boggle/score-words ?words) => ?exp)
    ?words                        ?exp
    ["" "ab" "abc"]               1
    ["abcdefgh" "abc" "abcd"]     13
    ["abcde" "abc" "abcd"]        4))

(facts "solution"
  (tabular
    (fact "can find words and score for a board"
      (let [result (boggle/solution ?board)]
        ; just ensure the number of words returned.
        (count (:words result)) => ?count
        (:max_score result) => ?score))
    ?board                ?count      ?score
    ["st" "ar"]           12          12
    ["sta" "rwa" "rse"]   34          41))
