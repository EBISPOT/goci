/*

################################################################################
Migration script to create a new CATALOG_UNPUBLISH_DATE in the HOUSEKEEPING
table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:    June 3rd 2015
version: 1.9.9.053 (pre 2.0)
################################################################################
*/

--------------------------------------------------------
--  ALTER HOUSEKEEPING
--------------------------------------------------------

ALTER TABLE "HOUSEKEEPING" ADD "CATALOG_UNPUBLISH_DATE" DATE;



