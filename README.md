# GOCI
GWAS Catalog Ontology and Curation Infrastructure from SPOT at EBI.

## Introduction

This project is a result of a collaboration between the NHGRI and the EBI to produce ontology-based curation and search
functionality for the GWAS catalog.  This includes ontology-based query expansion in the public interface and curator
tools for annotating studies as they are entered into the GWAS catalog.

## License

Copyright 2017 EMBL - European Bioinformatics Institute

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this software except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Building this Project

This project is built with Maven (http://maven.apache.org) so make sure you have an up-to-date installation of Maven
before proceeding.

Clone this project, change to the root GOCI directory and run

```bash
mvn clean install
```

to build all binaries of this project.

## Module Structure

The GWAS Ontology and Curation Infrastructure (GOCI) is organised into several main strands: tools for working with the ontology, tools for enhancing curation activities, and tools to generate a diagram of GWAS catalog data.

### GOCI Core

This module hosts the core classes underlying the GOCI tooling suite. It includes sub-modules for the key model objects that GOCI is based on, repository and service modules for accessing the data model and modules for diagram generation and interacting with ontologies.

### GOCI Interfaces

This module includes all the different ways to interface with the GWAS Catalog. It includes modules for the curation system, the public GWAS Catalog portal, the diagram generation service and a place holder module with some config for the GWAS Solr index.

### GOCI Tools

This module contains a range of stand-alone tools, including a datapublisher to convert the relational GWAS database into RDF/OWL, a mapper to annotate Catalog data with genomic context information from Ensembl via their REST API, a Solr indexer to load the database into a Solr index, as well as a range of util classes and one-off tools used for analysis.

### GOCI Parent & GOCI Dependencies

These are convenience modules used for dependency management.

## Acknowledgements

The GOCI project makes use of many public and freely available software resources - we would like to thank them all for their continued support.

* [Bamboo](http://atlassian.com/software/bamboo/overview): Continuous integration, continuous deployment and release management.

* [Fisheye](http://atlassian.com/software/fisheye/overview): Browse, search and track your source code repositories.

* [Spring](http://spring.io/): Application framework and inversion of control container for the Java platform

* [ThymeLeaf](http://www.thymeleaf.org/): Template engine capable of processing and generating HTML, XML, JavaScript, CSS and text, and can work both in web and non-web environments

* [Bootstrap](http://getbootstrap.com/): HTML, CSS, and JS framework for developing responsive, mobile first projects on the web.

* [OWL API](http://owlapi.sourceforge.net/): Java OWL API

* [HermiT](http://www.hermit-reasoner.com/): OWL reasoner

* [Solr](http://lucene.apache.org/solr/): Search server

* [OpenLink Virtuoso](http://virtuoso.openlinksw.com/): RDF triple store provider for SPARQL endpoint

* [Apache Tomcat](http://tomcat.apache.org/): Web server

* [Apache Maven](https://maven.apache.org/): Software library dependency management

* [GitHub](https://github.com/): source code hosting




