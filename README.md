
# GOCI
 GWAS Catalog Ontology and Curation Infrastructure from SPOT at EBI.


## Introduction

This project is a result of a collaboration between the NHGRI and the EBI to produce ontology-based curation and search
functionality for the GWAS catalog.  This includes ontology-based query expansion in the public interface and curator
tools for annotating studies as they are entered into the GWAS catalog.

Below is some developer information for working with the code held in this git repository.

## Git vs. SVN

We've opted to use git as the SCM for this project, and host the source code on GitHub.  The main motivation for this
is to enable us to use GitHub rather than sourceforge as our hosting site - it is much faster and provides better
support.

The nice thing about GitHub is that is provides access to the project sourcecode via git or SVN: so if you are not used
to working with git you can still contribute using SVN.

To obtain copies of the project source, use the following commands:

Git:

 git clone https://github.com/ebispot/goci

SVN:

 svn checkout https://github.com/ebispot/goci

You can push changes back to the GitHub repository using the normal mechanism for either git or SVN.

## Maven Structure

The GWAS Ontology and Curation Infrastructure (GOCI) is organised into several main strands: tools for working with the
ontology, tools for enhancing curation activities, and tools to generate a diagram of GWAS catalog data.

### GOCI Core

This module hosts the core classes underlying the GOCI tooling suite. It includes sub-modules for the key model objects that GOCI is based on, repository and service modules for accessing the data model and modules for diagram generation and interacting with ontologies.

### GOCI Interfaces

This module includes all the different ways to interface with the GWAS Catalog. It includes modules for the curation system, the public GWAS Catalog portal, the diagram generation service and a place holder module with some config for the GWAS Solr index.


### GOCI Tools

This module contains a range of stand-alone tools, including a datapublisher to convert the relational GWAS database into RDF/OWL, a mapper to annotate Catalog data with genomic context information from Ensembl via their REST API, a Solr indexer to load the database into a Solr index, as well as a range of util classes and one-off tools used for analysis.


### GOCI Parent & GOCI Dependencies

These are convenience modules used for dependency management.



##Building this Project

This project is built with Maven (http://maven.apache.org) so make sure you have an up-to-date installation of Maven
before proceeding.

Check out the source code as described above, change to the root GOCI directory and run

 mvn clean install

to build all binaries of this project.


