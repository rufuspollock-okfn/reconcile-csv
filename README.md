# reconcile-csv

A OpenRefine reconciliation service that works from CSV files.

## Introduction

Someone just handed you some datasets they claim are related and you should
be able to mesh them up easily - no problem you're the data wizard. Except
there is no clear unique identifier you could use to join the dataset. All
you know is that if several columns are the same, it should be the same.
Enter typos and misspellings and here you go: A long night ahead.

Reconcile-CSV aims to ease your pain: It acts as a OpenRefine
reconciliation service and performs fuzzy matching to identify related
entries.  

## Usage

Create a column with Unique-ID's you will use to match.

Pre-compiled:
```
java -jar reconcile-csv-1.0.1-SNAPSHOT-standalone.jar <file> <primary search column> <column with id's>
```

With Leiningen:
```
lein run <file> <primary search column> <column with id's>
```

Then add ```http://localhost:8000/reconcile``` as a reconciliation service
to refine. You can add more columns through the reconcile-interface in
Refine. 

Reconcile away!

Then use:

```
cell.recon.match.id
```

to get the ID from the match.


## License

Copyright © 2013 Michael Bauer, Open Knowledge Foundation

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
