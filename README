
  +---------------------------------------------------+
  |                                                   |
  | GWAS Catalog Ontology and Curation Infrastructure |
  |                                                   |
  +---------------------------------------------------+


  Introduction
  ============

This project is a result of a collaboration between the NHGRI and the EBI to produce ontology-based curation and search
functionality for the GWAS catalog.  This includes ontology-based query expansion in the public interface and curator
tools for annotating studies as they are entered into the GWAS catalog.

Below is some developer information for working with the code held in this git repository.

  Git vs. SVN
  ===========

We've opted to use git as the SCM for this project, and host the source code on GitHub.  The main motivation for this
is to enable us to use GitHub rather than sourceforge as our hosting site - it is much faster and provides better
support.

The nice thing about GitHub is that is provides access to the project sourcecode via git or SVN: so if you are not used
to working with git you can still contribute using SVN.

To obtain copies of the project source, use the following commands:

Git:

 git clone https://github.com/tburdett/goci

SVN:

 svn checkout https://github.com/tburdett/goci

You can push changes back to the GitHub repository using the normal mechanism for either git or SVN.

  Maven Structure
  ===============

The GWAS Ontology and Curation Infrastructure (GOCI) is organised into several main strands: tools for working with the
ontology, tools for enhancing curation activities, and tools to generate a diagram of GWAS catalog data.  The main GOCI
project contains three sub-projects reflecting these areas.

  GOCI Ontology
  -------------

The GOCI project uses EFO (http://www.ebi.ac.uk/efo) as it's ontology, so the ontology is not hosted within this
project.  We do however provide tools for working with this ontology, including javascript browser widgets for
displaying a list of GOCI-compliant tools in the browser

  GOCI Curation
  -------------

Curation tools are hosted in this directory.  This includes a PubMed Tracking system and a SNP Batch Loader.

The tracking system automatically searches PubMed for any potential papers describing genome wide association studies
and allows curators to flag those which are GWAS eligible, assign them to each other for checking, and generally record
curation provenance information.

The SNP Batch Loader is a standalone tool (designed as a Coldfusion extension, but which can be run from the command
line) designed to expedite the process of loading SNPs into the GWAS catalog database.  Rather than manually entering
data one by one, curators can create a spreadsheet (adhering to a given format) of the SNPs in a publication and upload
them to the database using this tool.

  GOCI Diagram
  ------------

Tools to automatically generate the GWAS catalog diagram will go here.

  Building this Project
  =====================

This project is built with Maven (http://maven.apache.org) so make sure you have an up-to-date installation of Maven
before proceeding.

Check out the source code as described above, change to the root GOCI directory and run

 mvn clean install

to build all binaries of this project.

  Performing a Release
  ====================

We will use the maven release plugin for this.  For clever stuff, see: http://www.ebi.ac.uk/seqdb/confluence/display/GOCI/Release+Process

