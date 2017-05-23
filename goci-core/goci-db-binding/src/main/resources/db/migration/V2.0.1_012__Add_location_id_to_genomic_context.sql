/*

################################################################################
Migration script to add LOCATION_ID column to GENOMIC_CONTEXT table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    July 20th 2015
version: 2.0.1.012
################################################################################

#######################################
#  ALTER GENOMIC_CONTEXT
#######################################
*/

ALTER TABLE "GENOMIC_CONTEXT" ADD "LOCATION_ID" NUMBER(19,0);

--------------------------------------------------------
--  Ref Constraints for Table STUDY
--------------------------------------------------------
  ALTER TABLE "GENOMIC_CONTEXT" ADD CONSTRAINT "GC_LOCATION_ID_FK" FOREIGN KEY ("LOCATION_ID")
	  REFERENCES "LOCATION" ("ID") ENABLE;
