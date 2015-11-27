/*
################################################################################
Migration script to update MAPPING_METADATA with new columns required for
Genome build version and dbSNP version

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    November 17th 2015
version: 2.0.1.038
################################################################################
*/

--------------------------------------------------------
--  ADD NEW COLUMN
--------------------------------------------------------

ALTER TABLE "MAPPING_METADATA" ADD "GENOME_BUILD_VERSION" VARCHAR2(255 CHAR);
ALTER TABLE "MAPPING_METADATA" ADD "DBSNP_VERSION" NUMBER(19,0);