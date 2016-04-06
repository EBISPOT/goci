/*
################################################################################
Migration script to update ASSOCIATION with new column required for or_type.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    April 4th 2016
version: 2.1.2.008
################################################################################
*/
--------------------------------------------------------
--  ADD ADDITIONAL LEGACY_OR_TYPE COLUMNS
--------------------------------------------------------

ALTER TABLE "ASSOCIATION" ADD "LEGACY_OR_TYPE" NUMBER(1,0);