/*

################################################################################
Migration script to remove a wrong constraint created in 002

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Tony Burdett
date:    January 21st 2015
version: 1.9.9.003 (pre 2.0)
################################################################################

*/

/*
###########################################
#  REMOVE WRONG CONSTRAINT ON JOIN TABLE  #
###########################################
*/

--------------------------------------------------------
--  Drop constraint on Table ASSOCIATION_EFO_TRAIT
--------------------------------------------------------
ALTER TABLE "ASSOCIATION_EFO_TRAIT" DROP CONSTRAINT "ASSOC_TRAIT_ID_UK";
DROP INDEX "ASSOC_TRAIT_ID_UK";
