/*

################################################################################
Migration script to accommodate a RISK_ALLELE_PROXY_SNP table,
designed to captured snps linked to a risk allele that have been
designated as a proxy snp

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    April 14th 2015
version: 1.9.9.035 (pre 2.0)
################################################################################

*/

/*
#######################################
#  CREATE NEW TABLES AND CONSTRAINTS  #
#######################################
*/

--------------------------------------------------------
--  DDL for Table RISK_ALLELE_PROXY_SNP
--------------------------------------------------------
CREATE TABLE "RISK_ALLELE_PROXY_SNP" (
    "RISK_ALLELE_ID" NUMBER(19,0),
    "SNP_ID" NUMBER(19,0));

--------------------------------------------------------
--  Constraints for Table RISK_ALLELE_PROXY_SNP
--------------------------------------------------------
ALTER TABLE "RISK_ALLELE_PROXY_SNP" MODIFY ("RISK_ALLELE_ID" NOT NULL ENABLE);
ALTER TABLE "RISK_ALLELE_PROXY_SNP" MODIFY ("SNP_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table RISK_ALLELE_PROXY_SNP
--------------------------------------------------------
ALTER TABLE "RISK_ALLELE_PROXY_SNP" ADD CONSTRAINT "PROXY_SNP_RA_ID_FK" FOREIGN KEY ("RISK_ALLELE_ID")
REFERENCES "RISK_ALLELE" ("ID") ENABLE;
ALTER TABLE "RISK_ALLELE_PROXY_SNP" ADD CONSTRAINT "PROXY_SNP_SNP_ID_FK" FOREIGN KEY ("SNP_ID")
REFERENCES "SINGLE_NUCLEOTIDE_POLYMORPHISM" ("ID") ENABLE;
