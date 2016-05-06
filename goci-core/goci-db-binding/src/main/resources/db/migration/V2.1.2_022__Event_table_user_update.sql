/*
################################################################################
Migration script to update user reference in EVENT table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author: Emma Hastings
date:    May 6th 2016
version: 2.1.2.022
################################################################################
*/
--------------------------------------------------------
--  Ref Constraints for Table EVENT
--------------------------------------------------------
ALTER TABLE "EVENT" ADD CONSTRAINT "EVENT_USER_ID_FK" FOREIGN KEY ("USER_ID")
REFERENCES "SECURE_USER" ("ID") ENABLE;