/*
################################################################################
Migration script to update ASSOCIATION with new column required for description.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    April 4th 2016
version: 2.1.2.006
################################################################################
*/
--------------------------------------------------------
--  ADD ADDITIONAL LEGACY_DESCRIPTION COLUMNS
--------------------------------------------------------

ALTER TABLE "ASSOCIATION" ADD "LEGACY_DESCRIPTION" VARCHAR2(255 CHAR);