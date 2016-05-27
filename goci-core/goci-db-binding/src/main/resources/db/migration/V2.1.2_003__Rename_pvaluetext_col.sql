/*
################################################################################
Migration script to rename PVALUE_TEXT table column

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatible with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    March 29th 2016
version: 2.1.2.003
################################################################################
*/
--------------------------------------------------------
-- ALTER ASSOCIATION
--------------------------------------------------------

ALTER TABLE "ASSOCIATION" RENAME COLUMN "PVALUE_TEXT" TO "PVALUE_DESCRIPTION";