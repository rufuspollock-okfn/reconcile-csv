---
page: index
layout: default
title: Reconcile-csv - join dirty data
---

# Reconcile-csv 
Joining datasets with fuzzy matching. 

## About
Do you know this? Finally you got two datasets containing data about the
same thing - all you need to do is join them up to produce your result.
Sometimes this is easier said then done: If unique identifiers are missing
and names are commonly spelled different, joining data becomes a nightmare.
*Reconcile-csv* aims to reduce this nightmare.

*Reconcile-csv* is a reconciliation service for
[OpenRefine](http://openrefine.org) running from a CSV file. It uses fuzzy
matching to match entries in one dataset to entries in another dataset,
helping to introduce unique IDs into the system - so they can be used to
join your data painlessly.

<a class="btn btn-primary btn-large" id="download" href="dist/reconcile-csv-0.1.1.jar">Download</a>
reconcile-csv-0.1.1

*Reconcile-csv* is free and open source software released under the [BSD
license](LICENSE). 

## Documentation

### Prerequisites

To use *reconcile-csv* you'll need:

* [OpenRefine](http://openrefine.org) - a fantastic tool to clean messy data
* Java - if OpenRefine runs on your machine, you already have it
* Two datasets you want to join that contain some columns where you can
  figure out whether items are the same.

Take one of the two datasets - preferably the more trusted, cleaner or
complete dataset - and introduce a column with unique identifiers (this can
simply be counting from 1 up, md5-sums of field combinations that are
unique or pre-existing unique ids in the dataset). 

### Starting the Server

To start the reconciliation server [download](#download) the jar file
above, then start it with:

```
java reconcile-csv-0.1.1.jar <CSV-File> <Search Column> <ID Column>
```

**CSV-File** is the csv file you will use as a basis of reconciliation. As
stated above preferably the cleaner, more complete or trusted one. You will
introduce the unique IDs from that file to the other file.

**Search Column** is the primary column you want to use for matching. E.g.
you want to match facilities with names spelled slightly differently - this
would be the column to add here.

**ID Column** is the column containing unique ids for the facilities - if
you don't have one: generate one. 

### Reconciling in OpenRefine

Start OpenRefine and load the dataset, where you want to introduce the
unique-ids from the other dataset. Select the column options for the column
containing the primary names for matching. this should be the same
information than the search column (e.g. if you specified a column of
facility names in the search, pick the column containing facility names). 

In the options select "Reconcile" and "Start Reconciling". Add a standard
reconciliation service pointing to *http://localhost:8000/reconcile*. The
service will offer you to add additional columns as parameters - if you
e.g. also have city names, you could add them and specify the column name
containing city names in the other CSV file. 

Start the reconciliation - depending on the size of your datasets this
might take a while. Fuzzy-matching is no easy feat, and each row in one
dataset has to be compared to each row in another dataset. 

When done OpenRefine will show you a facet that allows you to select the
score the entries got. Reconcile-csv uses a fuzzy-matching algorithm called
dice that returns the likelyhood the two compared entries are the same: if
it's 1 it means it's exactly the same and is automatically matched up. 

Look at your results often matches with very high scores (e.g. >0.85) can be
automatically matched to the highest scoring entry using the "action"
submeny in the reconciliation menu. For the others, go through manually, if
you like the link, click the check-mark. 

*Reconcile-csv* also supports searching for matches, if e.g. a match can't
be found but you happen to know the name of the facility in the other
dataset, you can click on "search for more" and enter the search term,
hovering over an entry will show you additional information to the term,
clicking will assign the match.

Once you found all the matches you can find- create the unique id column.
Do this by selecting "edit column -> add column based on this column" from
the options of the column where you did your matching. Now give the column
a name (the same as in the other spreadsheet helps increadibly) and use the
expression 

```
cell.recon.match.id
```

to get the IDs for the matches.

Now you can join the datasets using the unique ids

## Contribute

You can help to make *reconcile-csv* better in the next version. If you
find bugs or problems, please report them on the [github
issues](http://github.com/okfn/reconcile-csv/issues) - check whether
someone already reported the issue. 

If you know clojure (or want to learn it) feel free to fork the
[repository](http://github.com/okfn/reconcile-csv) and submit
pull-requests. Also look out for open issues you could resolve.

If you have any questions or want to discuss similar solutions: the
[OKFNLabs mailinglist]() or IRC Channel (#okfn on freenode) is the right
place.
