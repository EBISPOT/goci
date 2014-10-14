Lodestar
========

Lodestar is a Linked Data Browser and SPARQL endpoint. Lodestar is a Java based web app that can wrap any existing SPARQL endpoint to provide a set of additional SPARQL and Linked Data services. Lodestar was developed to provide a consistent set of SPARQL and Linked Data services across the European Bioinformatics Institute (EBI). Some of the service provided by Lodestar:

* Javascript based SPARQL endpoint with configurable example queries and paginated results table
* Read only SPARQL endpoint for protection from write operations
* A single SPARQL endpoint that provides a UI, the service and a SPARQL 1.1 service description
* SPARQL syntax highlighting provided by CodeMirror
* Works with any SPARQL endpoint (Includes Virtuoso JDBC connection option)
* Linked data browser for navigating data from a SPARQL endpoint
* Configurable resource description/linked data pages:
  * Renders resources by label where possible
  * Grouping of related resource by type
  * Set top facts to display, such as labels and descriptions
  * Configurable limits for how many related resources to render in the browser
* Renders depictions of resources
* Handles content negotiation for both SPARQL queries and linked data pages
* CORS enabled for cross domain scripting
* Basic REST API for accessing data in simplified JSON format

To see a demonstration of the Lodestar linked data browser please see the [Expression Atlas RDF website](http://www.ebi.ac.uk/rdf/services/atlas/sparql). Lodestar has been primarily developed as an internal tool for EBI services deploying RDF, however, the application should be sufficiently generic that others can use it. I can't guarantee any support for the software at this time, but please feel free to use it or adapt for your own purposes and let me know how you get on.

Documentation and stable release at http://www.ebi.ac.uk/fgpt/sw/lodestar.

Release Notes
=============

**1.2**  21st August 2013
* Updated to Jena 2.12
* Exposed JSON-LD support from Jena in UI 
* Moved virtuoso to separate module, only builds in "virtuoso" profile
* Removed dependencies on virtuoso inferencing rules
* SPARQL endpoint now support application/sparql-query POST requests 
* Fixed some browser rendering bugs 

**1.1** 27th November 2013	
* Updated to Jena 2.11
* Fixed query limit bug (RDF-10)
* Added config for query timeouts (RDF-15)
* Configurable hide RDFS button (RDF-7)
* Added servlet status monitor
* javascript cleanup

**1.0.2** 29th August 2013	
* Updated to Jena 2.10
* VirtJena JDBC 4 (includes support for SPARQL bind queries). Requires virtuoso 6.1.7.2
* Added CSV and TSV sparql results export
* Fixed sparql results offset caching from previous query
* Fixed virtuoso describe query not returning all triples from all graphs

**1.0.1** 5th August 2013
* First release





