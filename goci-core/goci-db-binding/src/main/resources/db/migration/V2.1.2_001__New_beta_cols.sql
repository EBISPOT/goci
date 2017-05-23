/*
################################################################################
Migration script to update ASSOCIATION with new columns required for
mapping pipeline.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    February 23th 2016
version: 2.1.2.001
################################################################################
*/
--------------------------------------------------------
--  ADD ADDITIONAL BETA COLUMNS
--------------------------------------------------------

ALTER TABLE "ASSOCIATION" ADD "BETA_NUM" FLOAT(126);
ALTER TABLE "ASSOCIATION" ADD "BETA_UNIT" VARCHAR2(255 CHAR);
ALTER TABLE "ASSOCIATION" ADD "BETA_DIRECTION" VARCHAR2(255 CHAR);