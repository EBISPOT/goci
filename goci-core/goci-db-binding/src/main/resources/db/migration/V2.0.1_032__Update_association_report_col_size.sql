/*
################################################################################
Migration script to update ASSOCIATION_REPORT column size.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    August 28th 2015
version: 2.0.1.032
################################################################################
*/

--------------------------------------------------------
--  ALTER NEW COLUMNS
--------------------------------------------------------

ALTER TABLE "ASSOCIATION_REPORT" MODIFY "REST_SERVICE_ERROR" VARCHAR2(4000 BYTE);
ALTER TABLE "ASSOCIATION_REPORT" MODIFY "SUSPECT_VARIATION_ERROR" VARCHAR2(4000 BYTE);