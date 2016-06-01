/*
################################################################################
Migration script to rename date col in EVENT table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author: Emma Hastings
date:    May 9th 2016
version: 2.1.2.023
################################################################################
*/
--------------------------------------------------------
--  Rename DATE column in Table EVENT
--------------------------------------------------------
ALTER TABLE "EVENT" RENAME COLUMN "DATE" TO "EVENT_DATE";