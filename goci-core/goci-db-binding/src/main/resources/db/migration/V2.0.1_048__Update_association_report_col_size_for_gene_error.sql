/*
################################################################################
Migration script to update ASSOCIATION_REPORT column size.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    Nov 24th 2015
version: 2.0.1.048
################################################################################
*/

--------------------------------------------------------
--  ALTER COLUMN
--------------------------------------------------------

ALTER TABLE "ASSOCIATION_REPORT" MODIFY "GENE_ERROR" VARCHAR2(4000 BYTE);
