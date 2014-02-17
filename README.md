# boggle

A [Boggle](http://en.wikipedia.org/wiki/Boggle) game board solver.

## Public Endpoint

http://young-atoll-6726.herokuapp.com/boggle

## Usage

A true Boggle board is represented by a JSON array of 4 string, each 4 letters long. Eg:

```'["abcd", "efgh" "ijkl" "mnop"]'```

However, the endpoint will accept any "square" array of even length strings. Eg:

```'["st", "ar"]'```

This array can be POSTed to the /boggle endpoint. eg (using 2x2 board for example):

    $ curl "http://young-atoll-6726.herokuapp.com/boggle" -d '["st", "ar"]' -s | python -mjson.tool
    {   
        "max_score": 12,
        "words": [
            "ars",
            "art",
            "ras",
            "rat",
            "tar",
            "tas",
            "sat",
            "rats",
            "tars",
            "arts",
            "tsar",
            "star"
        ]
    }

The result will contain all the words found in the given board, along with the maximum score of the board.

*Note:* Running with a 4x4 board can take several minutes to return. 

## Dictionary

All possible words are checked against a dictionary to determine if it is a valid English word.

The dictionary used is the [The fourth edition of the Official SCRABBLE Players Dictionary](http://svn.pietdepsi.com/repos/projects/zyzzyva/trunk/data/words/North-American/OSPD4.txt)

## Prequesites

### [Leiningen](https://github.com/technomancy/leiningen)

Version written against:

    $ lein -v
    Leiningen 2.3.4 on Java 1.6.0_51 Java HotSpot(TM) 64-Bit Server VM

## Running

To start a local web server for development you can either eval the
commented out forms at the bottom of `web.clj` from your editor or
launch from the command line:

    $ lein run -m boggle.web

## Testing

Tests written using [midje](https://github.com/marick/Midje). To execute:

    $ lein midje

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
