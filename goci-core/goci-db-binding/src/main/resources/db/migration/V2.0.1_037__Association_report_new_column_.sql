/*
################################################################################
Migration script to update ASSOCIATION_REPORT with new column required for
mapping pipeline.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    November 11th 2015
version: 2.0.1.037
################################################################################
*/
--------------------------------------------------------
--  DROP BOOLEAN COLUMN GENE_ERROR
--------------------------------------------------------

ALTER TABLE ASSOCIATION_REPORT DROP COLUMN GENE_ERROR;

--------------------------------------------------------
--  ADD NEW COLUMN
--------------------------------------------------------

ALTER TABLE "ASSOCIATION_REPORT" ADD "GENE_ERROR" VARCHAR2(255 CHAR);
