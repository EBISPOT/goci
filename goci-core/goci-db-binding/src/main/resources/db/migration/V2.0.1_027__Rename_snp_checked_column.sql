/*
################################################################################
Migration script to add rename ASSOCIATION table column SNP_CHECKED

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    August 4th 2015
version: 2.0.1.027
################################################################################
*/
--------------------------------------------------------
-- ALTER ASSOCIATION
--------------------------------------------------------

ALTER TABLE "ASSOCIATION" RENAME COLUMN "SNP_CHECKED" TO "SNP_APPROVED";