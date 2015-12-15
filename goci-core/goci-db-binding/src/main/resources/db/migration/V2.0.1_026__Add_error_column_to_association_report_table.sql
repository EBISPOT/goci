/*
################################################################################
Migration script to add column to ASSOCIATION_REPORT table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    August 3rd 2015
version: 2.0.1.026
################################################################################
*/

--------------------------------------------------------
-- ALTER ASSOCIATION_REPORT
--------------------------------------------------------

  ALTER TABLE "ASSOCIATION_REPORT" ADD "ERROR_CHECKED_BY_CURATOR" NUMBER(1,0);