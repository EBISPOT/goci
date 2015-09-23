/*

################################################################################
Migration script to adapt the most recent model to accomodate a LOCUS table,
designed to capture differences between multi-snp haplotypes, SNPxSNP analyses,
and straightforward single SNP associations

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Tony Burdett
date:    January 28st 2015
version: 1.9.9.007 (pre 2.0)
################################################################################

*/


/*
#######################################
#  CREATE NEW TABLES AND CONSTRAINTS  #
#######################################
*/

--------------------------------------------------------
--  DDL for Table LOCUS
--------------------------------------------------------
CREATE TABLE "LOCUS" (
    "ID" NUMBER(19,0),
    "HAPLOTYPE_SNP_COUNT" NUMBER(19,0),
    "DESCRIPTION" VARCHAR2(255 CHAR),
    "MIGRATED_DESCRIPTION" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Table RISK_ALLELE
--------------------------------------------------------
CREATE TABLE "RISK_ALLELE" (
    "ID" NUMBER(19,0),
    "RISK_ALLELE_NAME" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Table LOCUS_RISK_ALLELE
--------------------------------------------------------
CREATE TABLE "LOCUS_RISK_ALLELE" (
    "LOCUS_ID" NUMBER(19,0),
    "RISK_ALLELE_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table AUTHOR_REPORTED_GENE
--------------------------------------------------------
CREATE TABLE "AUTHOR_REPORTED_GENE" (
    "LOCUS_ID" NUMBER(19,0),
    "REPORTED_GENE_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table RISK_ALLELE_SNP
--------------------------------------------------------
CREATE TABLE "RISK_ALLELE_SNP" (
    "RISK_ALLELE_ID" NUMBER(19,0),
    "SNP_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table ASSOCIATION_LOCUS
--------------------------------------------------------
CREATE TABLE "ASSOCIATION_LOCUS" (
    "ASSOCIATION_ID" NUMBER(19,0),
    "LOCUS_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Index LOCUS_ID_PK
--------------------------------------------------------
CREATE UNIQUE INDEX "LOCUS_ID_PK" ON "LOCUS" ("ID");

--------------------------------------------------------
--  DDL for Index RISK_ALLELE_ID_PK
--------------------------------------------------------
CREATE UNIQUE INDEX "RISK_ALLELE_ID_PK" ON "RISK_ALLELE" ("ID");

--------------------------------------------------------
--  Constraints for Table LOCUS
--------------------------------------------------------
ALTER TABLE "LOCUS" ADD PRIMARY KEY ("ID") ENABLE;
ALTER TABLE "LOCUS" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table RISK_ALLELE
--------------------------------------------------------
ALTER TABLE "RISK_ALLELE" ADD PRIMARY KEY ("ID") ENABLE;
ALTER TABLE "RISK_ALLELE" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table LOCUS_RISK_ALLELE
--------------------------------------------------------
ALTER TABLE "LOCUS_RISK_ALLELE" MODIFY ("LOCUS_ID" NOT NULL ENABLE);
ALTER TABLE "LOCUS_RISK_ALLELE" MODIFY ("RISK_ALLELE_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table AUTHOR_REPORTED_GENE
--------------------------------------------------------
ALTER TABLE "AUTHOR_REPORTED_GENE" MODIFY ("LOCUS_ID" NOT NULL ENABLE);
ALTER TABLE "AUTHOR_REPORTED_GENE" MODIFY ("REPORTED_GENE_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table RISK_ALLELE_SNP
--------------------------------------------------------
ALTER TABLE "RISK_ALLELE_SNP" MODIFY ("RISK_ALLELE_ID" NOT NULL ENABLE);
ALTER TABLE "RISK_ALLELE_SNP" MODIFY ("SNP_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table RISK_ALLELE_SNP
--------------------------------------------------------
ALTER TABLE "ASSOCIATION_LOCUS" MODIFY ("ASSOCIATION_ID" NOT NULL ENABLE);
ALTER TABLE "ASSOCIATION_LOCUS" MODIFY ("LOCUS_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table LOCUS_RISK_ALLELE
--------------------------------------------------------
ALTER TABLE "LOCUS_RISK_ALLELE" ADD CONSTRAINT "LOCUS_RISK_ALLELE_LOCUS_ID_FK" FOREIGN KEY ("LOCUS_ID")
REFERENCES "LOCUS" ("ID") ENABLE;
ALTER TABLE "LOCUS_RISK_ALLELE" ADD CONSTRAINT "LOCUS_RISK_ALLELE_RA_ID_FK" FOREIGN KEY ("RISK_ALLELE_ID")
REFERENCES "RISK_ALLELE" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table AUTHOR_REPORTED_GENE
--------------------------------------------------------
ALTER TABLE "AUTHOR_REPORTED_GENE" ADD CONSTRAINT "AUTHOR_REP_GENE_LOCUS_ID_FK" FOREIGN KEY ("LOCUS_ID")
REFERENCES "LOCUS" ("ID") ENABLE;
ALTER TABLE "AUTHOR_REPORTED_GENE" ADD CONSTRAINT "AUTHOR_REP_GENE_GENE_ID_FK" FOREIGN KEY ("REPORTED_GENE_ID")
REFERENCES "GENE" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table RISK_ALLELE_SNP
--------------------------------------------------------
ALTER TABLE "RISK_ALLELE_SNP" ADD CONSTRAINT "RISK_ALLELE_SNP_RA_ID_FK" FOREIGN KEY ("RISK_ALLELE_ID")
REFERENCES "LOCUS" ("ID") ENABLE;
ALTER TABLE "RISK_ALLELE_SNP" ADD CONSTRAINT "RISK_ALLELE_SNP_SNP_ID_FK" FOREIGN KEY ("SNP_ID")
REFERENCES "SINGLE_NUCLEOTIDE_POLYMORPHISM" ("ID") ENABLE;

/*
###################################
#  CREATE SEQUENCES and TRIGGERS  #
###################################
*/

--------------------------------------------------------
-- Create trigger on LOCUS
--------------------------------------------------------
CREATE OR REPLACE TRIGGER LOCUS_TRG
BEFORE INSERT ON LOCUS
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER LOCUS_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on RISK_ALLELE
--------------------------------------------------------
CREATE OR REPLACE TRIGGER RISK_ALLELE_TRG
BEFORE INSERT ON RISK_ALLELE
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER RISK_ALLELE_TRG ENABLE;
