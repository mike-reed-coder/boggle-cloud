(ns boggle.boggle)

(defn load-dict*
  "Loads a file containing all valid words. Creates a map that has
   a key for every word in the dictionary, for fast lookups."
  []
  (let [dict (clojure.java.io/resource "words.txt")
        reader (clojure.java.io/reader dict)]
    (zipmap (line-seq reader) (repeat true))))

; Only load the actual dictonary file into memory once
(def load-dict (memoize load-dict*))

(defn is-word?
  "Returns true is the word is in the dictionary."
  [word]
  (and (>= (count word) 3) (get (load-dict) (.toUpperCase word))))

(defn arr-to-str
  "Helper to convert an aray of chars to a string."
  [arr]
  (apply str arr))

(defn str-to-arr
  "Converts a string to an vector of chars."
  [s]
  (vec (map identity s)))

(defn get-2d
  "Gets the value at x y from the given 2d matrix."
  [m x y]
  (nth (nth m y) x))

(defn assoc-2d
  "Assigns the value 'v' to given 2d matrix 'm', at the given x y."
  [m x y v]
  (let [n (count m)]
    (for [i (range n)]
      (let [row (nth m i)]
        (if (= i y)
          (arr-to-str (assoc (str-to-arr row) x v))
          row)))))

(defn safe-range
  "Helper to return a 'safe' range around the value z.
   Safe meaning: minimum of 0 and max of n."
  [z n]
  (range (max 0 (- z 1)) (min (+ z 2) n)))

(defn neighbors
  "Finds all the non-marked neighbors to the given x y.
   A neighbor is horizontal, vertical, or diagonal to the given x y."
  [board x y]
  (let [n (count board)
        neighbors (for [i (safe-range x n)]
                    (for [j (safe-range y n)]
                      (when (and (not (and (= i x) (= j y)))
                                 (not= (get-2d board i j) \0))
                        [i j])))]
    (apply concat (map #(keep identity %) neighbors))))

(defn depth-find
  "Depth first search that finds all words in the given Boggle board,
   starting with the letter at x y."
  ([board x y] (depth-find board "" #{} x y))
  ([board word words x y]
   (let [word (str word (get-2d board x y))
         words (if (is-word? word) (cons word words) words)
         board (assoc-2d board x y 0) ; mark used letters as '0'
         neighbors (neighbors board x y)]
     (if-not (seq neighbors)
       (seq words)
       (keep identity (flatten (map #(apply (partial depth-find board word words) %) neighbors)))))))

(defn find-all-words
  "Fins all words in the given Boggle words. Uses a depth-first search
   on each letter in the board, to find all words starting with that letter."
  [board]
  (let [n (count board)]
    (into #{} (flatten (for [x (range n)]
                         (for [y (range n)]
                           (depth-find board x y)))))))

(defn score
  "Rturns a Boggle score for a single word."
  [word]
  (condp apply [(count word)]
    #(>= % 8) 11
    #(= % 7) 5
    #(= % 6) 3
    #(= % 5) 2
    #(>= % 3) 1
    identity 0))

(defn score-words
  "Sums up the score of all the given words."
  [words]
  (reduce + (map score words)))

(defn solution
  "Returns the full solution to the given Boggle board, including all words found
   and the max Boggle score."
  [board]
  (let [words (find-all-words board)]
    {:words words
     :max_score (score-words words)}))

(defn valid-board?
  "Returns truthy if the given board is valid.
   A board must be a n-length sequence of n-length strings."
  [board]
  (and (seq board)
       (every? string? board)
       (every? #(= (count %) (count board)) board)))

(defn solve-board
  "'Solves' the given board, by finding all English words, by the rules of Boggle: "
  [board]
  (if (valid-board? board)
    {:status 200 :headers {} :body (solution board)}
    {:status 422 :headers {} :body "Invalid board provided. Please provide an array with with N strings of length N (a square)."}))
