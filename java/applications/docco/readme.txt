Docco Readme File
========================

Welcome to Docco, a little indexing program build on top of the Lucene search
engine from the Apache Foundation (http://www.apache.org). It indexes the
documents of a folder in your file system recursively and then lets you
query the contents. The results are displayed in a structured form using a
technique called Formal Concept Analysis.

This is still a very early release, but it should work. A quick start manual
for the program follows below. You find the licence for the program in the
main folder and the licences for the libraries used in the folder named "libs".

Enjoy the program and feel free to send feedback to tockit-general@lists.sf.net.




Quick Start
===========

(1) Starting the Program

There are start scripts for MS Windows and UNIX/Linux, but the only thing you
really have to do is to execute the JAR file using "java -jar Docco.jar" or
a similar call.

(2) Creating an Index

The first thing that happens if you start the program for the first time should 
be the appearance of a file open dialog that lets you pick a directory. Pick any, 
click the "Index Directory" button and wait. This might take quite a while,
depending on many factors, most noticably the number of files involved. You might
want to try with a smaller structure first before you run it on your companies
main network drive.

At the moment we index the following document types:
- plain text (*.txt and some programming language extensions)
- HTML documents (*.html, *.htm)
- PDF documents (*.pdf)

(3) Querying

Just enter some keywords you are looking for into the text field and hit the Enter
key. That is pretty much all you need to do. You can also use:

  - a minus sign or exclamation mark to negate a keyword, i.e. look for those
    documents NOT containing a keyword
  - an asterisk as a wildcard, e.g. "pres*" would match "president", "prestige"
    and other words
  - parentheses to group expressions in the display -- see below
  - double quotes to search for phrases

You can actually use most of the syntax supported by Lucene, but these are the
most important ones and some of the rest is beyond the scope of this little
introduction. See http://jakarta.apache.org/lucene/docs/queryparsersyntax.html
for more detail.

(4) Reading the Results

The result of a query is a diagram showing you how the query parts combine. Each
query is broken down into parts, in the case of simple keywords each keyword
represents a part. Parentheses and quotes group more complex expressions into a
single part.

The diagram contains a top node showing the number of all documents found that
match anything in your query. Below that you can see nodes showing you which
documents hit which part of your query. These are then combined more and more to
show you how the query result sets interlap.

You can hit on any node in the diagram and you will get a list of the results on
the right hand side, displayed as a tree. Selecting a document in this tree view
shows you more information at the bottom of the screen. Double-clicking a document
in the tree view or clicking the button at the bottom of the lower display area
opens the document in the default application.

========= that was the important stuff you really should know ==============

(5) Display Options

(5.1) Phantom Nodes

Sometimes you will find that your data has implications of the form "every
document containg the keywords A and B also contains C". For example in some
document collection all documents containing "program" and "source" also contain
"code". If you query for all three keywords, you will find that Docco displays
the node showing the combination of "program" and "source" but nothing else as a 
smaller node. This combination does not really exist and therefore the node is not
needed structurally, it is just added as layout addition to make the diagram more
structured. You can turn these nodes off, which gives smaller diagrams at the
expense of less structure. This is usually helpful if your query returns many
phantom nodes.

(5.2) Showing Matches Only Once

Documents that match "a" and "b" also match just "a". Therefore they will appear
at least at two nodes: the one for "a" and the one for ("a" and "b"). You can
reduce the display by only showing the documents which match only the queries for
a node but nothing else. In the example the documents matching "a" and "b" would
not be shown at the "a" node, since this node would show only the documents matching
"a" but nothing else. This option can be changed in the user interface and affects
the diagram and the contents of the tree view.

(6) Managing Indexes

Short answer: not really yet. The only thing you can do is deleting the directory
".doccoIndex" in the installation directory and restart the program. Then you will
get asked about a directory to index again.

We will offer you a way to manage indexes (indices?) and the indexers one day, just
not yet in this early release.