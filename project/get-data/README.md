A library for scraping trip report data from various websites.

## Running

```bash
clj -A:repl -i
```

```clojure
(require '[scrape-trs.core :refer [get-trip-reports!]]) 

(def summitpost-rainier-trs (str 
   "https://www.summitpost.org/object_list.php?object_type=5&search_select_5=name_only&map_5=0&contributor_id=&order_type_5=DESC&object_name_5=rainier&state_province_5=Washington&order_type_5=DESC"))

(get-trip-reports! summitpost-rainier-trs "../data/trip-reports/summitpost-rainier.csv")
```

## Developing

The `:repl` alias runs an nrepl with cider-middleware so it is suitable for
connection to from an editor. To run tests:

```bash
clj -A:test
```
