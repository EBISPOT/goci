/*
################################################################################
Migration script to update MAPPING_METADATA table and change ENSEMBL_RELEASE_NUMBER
type.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatible with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    Sept 29th 2015
version: 2.0.1.035
################################################################################

#######################################
#  ALTER GENOMIC_CONTEXT
#######################################
*/

ALTER TABLE "MAPPING_METADATA" MODIFY "ENSEMBL_RELEASE_NUMBER" NUMBER(10,0);
