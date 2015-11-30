/*
################################################################################
Migration script to update ASSOCIATION_REPORT with new columns required for
mapping pipeline.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    August 28th 2015
version: 2.0.1.031
################################################################################
*/
--------------------------------------------------------
--  DROP SNP_PENDING COLUMN
--------------------------------------------------------

ALTER TABLE ASSOCIATION_REPORT DROP COLUMN SNP_PENDING;
ALTER TABLE ASSOCIATION_REPORT DROP COLUMN GENE_NOT_ON_GENOME;

--------------------------------------------------------
--  ADD NEW COLUMNS
--------------------------------------------------------

ALTER TABLE "ASSOCIATION_REPORT" ADD "REST_SERVICE_ERROR" VARCHAR2(255 CHAR);
ALTER TABLE "ASSOCIATION_REPORT" ADD "SUSPECT_VARIATION_ERROR" VARCHAR2(255 CHAR);