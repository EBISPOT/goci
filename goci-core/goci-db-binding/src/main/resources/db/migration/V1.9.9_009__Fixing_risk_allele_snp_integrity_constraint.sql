/*

################################################################################
Migration script to fix an integrity constraint bug in V1.9.9_007

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Tony Burdett
date:    January 28st 2015
version: 1.9.9.009 (pre 2.0)
################################################################################

*/


--------------------------------------------------------
--  Ref Constraints for Table RISK_ALLELE_SNP
--------------------------------------------------------
ALTER TABLE "RISK_ALLELE_SNP" DROP CONSTRAINT "RISK_ALLELE_SNP_RA_ID_FK";
ALTER TABLE "RISK_ALLELE_SNP" ADD CONSTRAINT "RISK_ALLELE_SNP_RA_ID_FK" FOREIGN KEY ("RISK_ALLELE_ID")
REFERENCES "RISK_ALLELE" ("ID") ENABLE;

