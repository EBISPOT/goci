/*

################################################################################
Migration script to add a column for OrPerCopyRecipRange to table Association

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:    April 22nd 2015
version: 1.9.9.038 (pre 2.0)
################################################################################

--------------------------------------------------------
--  Add OR_PER_COPY_RECIP_RANGE to ASSOCIATION
--------------------------------------------------------
*/


  ALTER TABLE "ASSOCIATION" ADD "OR_PER_COPY_RECIP_RANGE" VARCHAR2(255 CHAR);
