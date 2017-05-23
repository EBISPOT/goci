/*
################################################################################
Migration script to create MAPPING_METADATA table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatible with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    Sept 29th 2015
version: 2.0.1.034
################################################################################

#######################################
#  CREATE NEW TABLES AND CONSTRAINTS
#######################################
*/
--------------------------------------------------------
--  Create Table MAPPING_METADATA
--------------------------------------------------------

  CREATE TABLE "MAPPING_METADATA" (
     "ID" NUMBER(19,0),
     "ENSEMBL_RELEASE_NUMBER" VARCHAR2(255 CHAR),
     "USAGE_START_DATE" TIMESTAMP(6));

--------------------------------------------------------
--  DDL for Index MAPPING_METADATA_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "MAPPING_METADATA_ID_PK" ON "MAPPING_METADATA" ("ID");

--------------------------------------------------------
--  Constraints for Table MAPPING_METADATA
--------------------------------------------------------
  ALTER TABLE "MAPPING_METADATA" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "MAPPING_METADATA" MODIFY ("ID" NOT NULL ENABLE);