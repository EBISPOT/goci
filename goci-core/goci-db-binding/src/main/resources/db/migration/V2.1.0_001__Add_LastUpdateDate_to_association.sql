/*
################################################################################
Migration script to add LAST_UPDATE_DATE to ASSOCIATION table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    Dec 7th 2015
version: 2.1.0.001
################################################################################
*/
--------------------------------------------------------
--  ADD COLUMN
--------------------------------------------------------

ALTER TABLE "ASSOCIATION" ADD "LAST_UPDATE_DATE" DATE;
