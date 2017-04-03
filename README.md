# kixi.nybling

A tiny library that converts between formats.

``` clojure
[kixi/kixi.nybling "<version>"]
```

## Functions

``` clojure
(edn-str-to-json-str "{:foo \"bar\"}")
=> "{\"foo\":\"bar\"}"
```

## Deployment

To send to Clojars with AOT:
``` bash
lein with-profile uberjar deploy clojars
```

## License

Copyright © 2017 MastodonC Ltd

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
