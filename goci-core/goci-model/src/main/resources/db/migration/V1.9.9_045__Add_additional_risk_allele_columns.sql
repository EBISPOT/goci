/*

################################################################################
Migration script to add a column for GENOME_WIDE, LIMITED_LIST and RISK_FREQUENCY
to RISK_ALLELE TABLE

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    May 22nd 2015
version: 1.9.9.045 (pre 2.0)
################################################################################

--------------------------------------------------------
--  Add columns TO RISK_ALLELE table
--------------------------------------------------------
*/

  ALTER TABLE "RISK_ALLELE" ADD "GENOME_WIDE" NUMBER(1,0);
  ALTER TABLE "RISK_ALLELE" ADD "LIMITED_LIST" NUMBER(1,0);
  ALTER TABLE "RISK_ALLELE" ADD "RISK_FREQUENCY" VARCHAR2(255 CHAR);