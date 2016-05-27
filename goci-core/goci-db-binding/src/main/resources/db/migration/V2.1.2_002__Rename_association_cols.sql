/*
################################################################################
Migration script to rename ASSOCIATION table columns

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatible with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    February 23th 2016
version: 2.1.2.002
################################################################################
*/
--------------------------------------------------------
-- ALTER ASSOCIATION
--------------------------------------------------------

ALTER TABLE "ASSOCIATION" RENAME COLUMN "OR_PER_COPY_STD_ERROR" TO "STANDARD_ERROR";
ALTER TABLE "ASSOCIATION" RENAME COLUMN "OR_PER_COPY_UNIT_DESCR" TO "DESCRIPTION";
ALTER TABLE "ASSOCIATION" RENAME COLUMN "OR_PER_COPY_RANGE" TO "RANGE";