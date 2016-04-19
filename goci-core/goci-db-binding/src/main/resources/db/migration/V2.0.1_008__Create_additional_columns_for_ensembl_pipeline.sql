/*

################################################################################
Migration script to create:

- ENSEMBL_GENE_ID column in GENE table
- MAPPING_METHOD column in GENOMIC_CONTEXT
- SOURCE column in GENOMIC_CONTEXT

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    July 10th 2015
version: 2.0.1.008
################################################################################

#######################################
#  ALTER GENE AND GENOMIC_CONTEXT
#######################################
*/

ALTER TABLE "GENE" ADD "ENSEMBL_GENE_ID" VARCHAR2(255 CHAR);
ALTER TABLE "GENOMIC_CONTEXT" ADD "SOURCE" VARCHAR2(255 CHAR);
ALTER TABLE "GENOMIC_CONTEXT" ADD "MAPPING_METHOD" VARCHAR2(255 CHAR);
