Lodestar
========

Lodestar provides a Linked Data Browser and SPARQL endpoint. Lodestar is a Java based web app that can wrap any existing SPARQL endpoint to provide a set of additional SPARQL and Linked Data services. Lodestar was developed to provide a consistent set of SPARQL and Linked Data services across the European Bioinformatics Institute (EBI). Some of the service provided by Lodestar:

* Javascript based SPARQL endpoint with configurable example queries and paginated results table
* Read only SPARQL endpoint for protection from DELETE and UPDATE operations
* A single SPARQL endpoint URL that supports content-negotiation for SPARQL 1.1 service description
* SPARQL syntax highlighting provided by CodeMirror
* Wraps any SPARQL endpoint (Includes Virtuoso via JDBC connection)
* Linked data browser for navigation of resources inside any SPARQL endpoint
* Configurable resource description/linked data pages:
* * Renders resources by label where possible
* * Grouping of related resource by type
* * Set top relationships to display first, such as labels and descriptions
* * Configurable limits for how many related resources to render in the browser
* Renders depictions of resources
* Handles content negotiation for both SPARQL queries and linked data pages
* CORS enabled
* Simple REST API for accessing data in simplified JSON format

To see a demonstration of the Lodestar linked data browser please the Expression Atlas RDF website. Lodestar has been primarily developed as an internal tool for EBI resources, however, the application should be sufficiently generic that others can use and adopt it as they see fit. We can't guarantee any support for the software at this time, but please feel free to use it or adapt for your own purposes and let us know how you get on.

Documentation and stable release at http://www.ebi.ac.uk/fgpt/sw/lodestar. View a demo at http://www.ebi.ac.uk/fgpt/atlasrdf/sparql

