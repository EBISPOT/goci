/*

################################################################################
Migration script to accommodate a SNP_MERGED_SNP table,
designed to captured snps that have been merged into another SNP in a
 newer genome build

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:    May 26th 2015
version: 1.9.9.047 (pre 2.0)
################################################################################

*/

/*
#######################################
#  CREATE NEW TABLES AND CONSTRAINTS  #
#######################################
*/

--------------------------------------------------------
--  DDL for Table SNP_MERGED_SNP
--------------------------------------------------------
CREATE TABLE "SNP_MERGED_SNP" (
    "SNP_ID_MERGED" NUMBER(19,0),
    "SNP_ID_CURRENT" NUMBER(19,0));

--------------------------------------------------------
--  Constraints for Table SNP_MERGED_SNP
--------------------------------------------------------
ALTER TABLE "SNP_MERGED_SNP" MODIFY ("SNP_ID_MERGED" NOT NULL ENABLE);
ALTER TABLE "SNP_MERGED_SNP" MODIFY ("SNP_ID_CURRENT" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table SNP_MERGED_SNP
--------------------------------------------------------
ALTER TABLE "SNP_MERGED_SNP" ADD CONSTRAINT "SNP_MERGED_SNP_ID_FK" FOREIGN KEY ("SNP_ID_MERGED")
REFERENCES "SINGLE_NUCLEOTIDE_POLYMORPHISM" ("ID") ENABLE;
ALTER TABLE "SNP_MERGED_SNP" ADD CONSTRAINT "SNP_CURRENT_SNP_ID_FK" FOREIGN KEY ("SNP_ID_CURRENT")
REFERENCES "SINGLE_NUCLEOTIDE_POLYMORPHISM" ("ID") ENABLE;
