/*

################################################################################
Migration script to convert NHGRI GWAS database dump to revised EBI schema.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Tony Burdett
date:    January 9th 2015
version: 1.9.9.002 (pre 2.0)
################################################################################

*/


/*
#######################################
#  CREATE NEW TABLES AND CONSTRAINTS  #
#######################################
*/

--------------------------------------------------------
--  DDL for Table ASSOCIATION
--------------------------------------------------------
  CREATE TABLE "ASSOCIATION" (
      "ID" NUMBER(19,0),
      "ALLELE" VARCHAR2(255 CHAR),
      "AUTHOR_REPORTED_GENE" VARCHAR2(255 CHAR),
      "MULTI_SNP_HAPLOTYPE" VARCHAR2(255 CHAR),
      "OR_PER_COPY_NUM" FLOAT(126),
      "OR_PER_COPY_RANGE" VARCHAR2(255 CHAR),
      "OR_PER_COPY_RECIP" FLOAT(126),
      "OR_PER_COPY_STD_ERROR" FLOAT(126),
      "OR_PER_COPY_UNIT_DESCR" VARCHAR2(255 CHAR),
      "OR_TYPE" VARCHAR2(255 CHAR),
      "PVALUE_EXPONENT" NUMBER(10,0),
      "PVALUE_FLOAT" FLOAT(126),
      "PVALUE_MANTISSA" NUMBER(10,0),
      "PVALUE_TEXT" VARCHAR2(255 CHAR),
      "RISK_FREQUENCY" VARCHAR2(255 CHAR),
      "SNP_INTERACTION" VARCHAR2(255 CHAR),
      "SNP_TYPE" VARCHAR2(255 CHAR),
      "STRONGEST_ALLELE" VARCHAR2(255 CHAR),
      "STUDY_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table ASSOCIATION_EFO_TRAIT
--------------------------------------------------------
  CREATE TABLE "ASSOCIATION_EFO_TRAIT" (
      "ASSOCIATION_ID" NUMBER(19,0),
      "EFO_TRAIT_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table ASSOCIATION_REPORTED_GENE
--------------------------------------------------------
  CREATE TABLE "ASSOCIATION_REPORTED_GENE" (
      "ASSOCIATION_ID" NUMBER(19,0),
      "REPORTED_GENE_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table ASSOCIATION_SNP
--------------------------------------------------------
  CREATE TABLE "ASSOCIATION_SNP" (
      "ASSOCIATION_ID" NUMBER(19,0),
      "SNP_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table COUNTRY
--------------------------------------------------------
  CREATE TABLE "COUNTRY" (
      "ID" NUMBER(19,0),
      "MAJOR_AREA" VARCHAR2(255 CHAR),
      "NAME" VARCHAR2(255 CHAR),
      "REGION" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Table CURATION_STATUS
--------------------------------------------------------
  CREATE TABLE "CURATION_STATUS" (
      "ID" NUMBER(19,0),
      "SEQNBR" VARCHAR2(255 CHAR),
      "STATUS" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Table CURATOR
--------------------------------------------------------
  CREATE TABLE "CURATOR" (
      "ID" NUMBER(19,0),
      "EMAIL" VARCHAR2(255 CHAR),
      "FIRST_NAME" VARCHAR2(255 CHAR),
      "LAST_NAME" VARCHAR2(255 CHAR),
      "USER_NAME" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Table DISEASE_TRAIT
--------------------------------------------------------
  CREATE TABLE "DISEASE_TRAIT" (
      "ID" NUMBER(19,0),
      "TRAIT" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Table EFO_TRAIT
--------------------------------------------------------
  CREATE TABLE "EFO_TRAIT" (
      "ID" NUMBER(19,0),
      "TRAIT" VARCHAR2(255 CHAR),
      "URI" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Table ETHNICITY
--------------------------------------------------------
  CREATE TABLE "ETHNICITY" (
      "ID" NUMBER(19,0),
      "COUNTRY_OF_ORIGIN" VARCHAR2(255 CHAR),
      "COUNTRY_OF_RECRUITMENT" VARCHAR2(255 CHAR),
      "DESCRIPTION" VARCHAR2(4000 BYTE),
      "ETHNIC_GROUP" VARCHAR2(255 CHAR),
      "NOTES" VARCHAR2(4000 BYTE),
      "NUMBER_OF_INDIVIDUALS" NUMBER(10,0),
      "PREVIOUSLY_REPORTED" VARCHAR2(255 CHAR),
      "SAMPLE_SIZES_MATCH" VARCHAR2(255 CHAR),
      "TYPE" VARCHAR2(255 CHAR),
      "STUDY_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table GENE
--------------------------------------------------------
  CREATE TABLE "GENE" (
      "ID" NUMBER(19,0),
      "GENE_NAME" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Table HOUSEKEEPING
--------------------------------------------------------
  CREATE TABLE "HOUSEKEEPING" (
      "ID" NUMBER(19,0),
      "CHECKEDNCBIERROR" VARCHAR2(255 CHAR),
      "ETHNICITY_BACK_FILLED" VARCHAR2(255 CHAR),
      "ETHNICITY_CHECKED_LEVEL_ONE" VARCHAR2(255 CHAR),
      "ETHNICITY_CHECKED_LEVEL_TWO" VARCHAR2(255 CHAR),
      "FILE_NAME" VARCHAR2(255 CHAR),
      "LAST_UPDATE_DATE" DATE,
      "NOTES" VARCHAR2(4000 BYTE),
      "PUBLISH_DATE" DATE,
      "SEND_TONCBIDATE" DATE,
      "STUDY_ADDED_DATE" DATE,
      "STUDY_SNP_CHECKED_LEVEL_ONE" VARCHAR2(255 CHAR),
      "STUDY_SNP_CHECKED_LEVEL_TWO" VARCHAR2(255 CHAR),
      "CURATION_STATUS_ID" NUMBER(19,0),
      "CURATOR_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table REGION
--------------------------------------------------------
  CREATE TABLE "REGION" (
      "ID" NUMBER(19,0),
      "NAME" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Table SINGLE_NUCLEOTIDE_POLYMORPHISM
--------------------------------------------------------

  CREATE TABLE "SINGLE_NUCLEOTIDE_POLYMORPHISM" (
      "ID" NUMBER(19,0),
      "CHROMOSOME_NAME" VARCHAR2(255 CHAR),
      "CHROMOSOME_POSITION" VARCHAR2(255 CHAR),
      "LAST_UPDATE_DATE" TIMESTAMP (6),
      "RS_ID" VARCHAR2(255 CHAR));
--------------------------------------------------------
--  DDL for Table SNP_GENE
--------------------------------------------------------
  CREATE TABLE "SNP_GENE" (
      "SNP_ID" NUMBER(19,0),
      "GENE_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table SNP_REGION
--------------------------------------------------------
  CREATE TABLE "SNP_REGION" (
      "SNP_ID" NUMBER(19,0),
      "REGION_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table STUDY
--------------------------------------------------------
  CREATE TABLE "STUDY" (
      "ID" NUMBER(19,0),
      "AUTHOR" VARCHAR2(255 CHAR),
      "CNV" VARCHAR2(255 CHAR),
      "GXE" VARCHAR2(255 CHAR),
      "GXG" VARCHAR2(255 CHAR),
      "INITIAL_SAMPLE_SIZE" VARCHAR2(4000 BYTE),
      "PLATFORM" VARCHAR2(255 CHAR),
      "PUBLICATION" VARCHAR2(255 CHAR),
      "PUBMED_ID" VARCHAR2(255 CHAR),
      "REPLICATE_SAMPLE_SIZE" VARCHAR2(4000 BYTE),
      "STUDY_DATE" DATE,
      "TITLE" VARCHAR2(4000 BYTE),
      "HOUSEKEEPING_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table STUDY_DISEASE_TRAIT
--------------------------------------------------------
  CREATE TABLE "STUDY_DISEASE_TRAIT" (
      "DISEASE_TRAIT_ID" NUMBER(19,0),
      "STUDY_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table STUDY_EFO_TRAIT
--------------------------------------------------------
  CREATE TABLE "STUDY_EFO_TRAIT" (
      "STUDY_ID" NUMBER(19,0),
      "EFO_TRAIT_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table STUDY_SNP
--------------------------------------------------------
  CREATE TABLE "STUDY_SNP" (
      "STUDY_ID" NUMBER(19,0),
      "SNP_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Index ASSOCIATION_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "ASSOCIATION_ID_PK" ON "ASSOCIATION" ("ID");

--------------------------------------------------------
--  DDL for Index COUNTRY_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "COUNTRY_ID_PK" ON "COUNTRY" ("ID");

--------------------------------------------------------
--  DDL for Index CURATION_STATUS_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "CURATION_STATUS_ID_PK" ON "CURATION_STATUS" ("ID");

--------------------------------------------------------
--  DDL for Index CURATOR_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "CURATOR_ID_PK" ON "CURATOR" ("ID");

--------------------------------------------------------
--  DDL for Index DISEASE_TRAIT_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "DISEASE_TRAIT_ID_PK" ON "DISEASE_TRAIT" ("ID");

--------------------------------------------------------
--  DDL for Index EFO_TRAIT_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "EFO_TRAIT_ID_PK" ON "EFO_TRAIT" ("ID");

--------------------------------------------------------
--  DDL for Index ETHNICITY_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "ETHNICITY_ID_PK" ON "ETHNICITY" ("ID");

--------------------------------------------------------
--  DDL for Index GENE_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "GENE_ID_PK" ON "GENE" ("ID");

--------------------------------------------------------
--  DDL for Index HOUSEKEEPING_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "HOUSEKEEPING_ID_PK" ON "HOUSEKEEPING" ("ID");

--------------------------------------------------------
--  DDL for Index REGION_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "REGION_ID_PK" ON "REGION" ("ID");

--------------------------------------------------------
--  DDL for Index SNP_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "SNP_ID_PK" ON "SINGLE_NUCLEOTIDE_POLYMORPHISM" ("ID");

--------------------------------------------------------
--  DDL for Index STUDY_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "STUDY_ID_PK" ON "STUDY" ("ID");

--------------------------------------------------------
--  DDL for Index STUDY_DISEASE_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "STUDY_DISEASE_TRAIT_ID_UK" ON "STUDY_DISEASE_TRAIT" ("STUDY_ID");

--------------------------------------------------------
--  DDL for Index ASSOCIATION_REPORTED_GENE_ID_UK
--------------------------------------------------------
  CREATE UNIQUE INDEX "ASSOC_REP_GENE_ID_UK" ON "ASSOCIATION_REPORTED_GENE" ("REPORTED_GENE_ID");

--------------------------------------------------------
--  DDL for Index ASSOCIATION_EFO_TRAIT_ID_UK
--------------------------------------------------------
  CREATE UNIQUE INDEX "ASSOC_TRAIT_ID_UK" ON "ASSOCIATION_EFO_TRAIT" ("EFO_TRAIT_ID");

--------------------------------------------------------
--  Constraints for Table ASSOCIATION
--------------------------------------------------------
  ALTER TABLE "ASSOCIATION" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "ASSOCIATION" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table ASSOCIATION_EFO_TRAIT
--------------------------------------------------------
  ALTER TABLE "ASSOCIATION_EFO_TRAIT" ADD CONSTRAINT "ASSOC_TRAIT_ID_UK" UNIQUE ("EFO_TRAIT_ID") ENABLE;
  ALTER TABLE "ASSOCIATION_EFO_TRAIT" MODIFY ("EFO_TRAIT_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSOCIATION_EFO_TRAIT" MODIFY ("ASSOCIATION_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table ASSOCIATION_REPORTED_GENE
--------------------------------------------------------
  ALTER TABLE "ASSOCIATION_REPORTED_GENE" ADD CONSTRAINT "ASSOC_REP_GENE_ID_UK" UNIQUE ("REPORTED_GENE_ID") ENABLE;
  ALTER TABLE "ASSOCIATION_REPORTED_GENE" MODIFY ("REPORTED_GENE_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSOCIATION_REPORTED_GENE" MODIFY ("ASSOCIATION_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table ASSOCIATION_SNP
--------------------------------------------------------
  ALTER TABLE "ASSOCIATION_SNP" MODIFY ("SNP_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSOCIATION_SNP" MODIFY ("ASSOCIATION_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table COUNTRY
--------------------------------------------------------
  ALTER TABLE "COUNTRY" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "COUNTRY" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table CURATION_STATUS
--------------------------------------------------------
  ALTER TABLE "CURATION_STATUS" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "CURATION_STATUS" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table CURATOR
--------------------------------------------------------
  ALTER TABLE "CURATOR" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "CURATOR" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table DISEASE_TRAIT
--------------------------------------------------------
  ALTER TABLE "DISEASE_TRAIT" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "DISEASE_TRAIT" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table EFO_TRAIT
--------------------------------------------------------
  ALTER TABLE "EFO_TRAIT" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "EFO_TRAIT" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table ETHNICITY
--------------------------------------------------------
  ALTER TABLE "ETHNICITY" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "ETHNICITY" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table GENE
--------------------------------------------------------
  ALTER TABLE "GENE" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "GENE" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table HOUSEKEEPING
--------------------------------------------------------
  ALTER TABLE "HOUSEKEEPING" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "HOUSEKEEPING" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table REGION
--------------------------------------------------------
  ALTER TABLE "REGION" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "REGION" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table SINGLE_NUCLEOTIDE_POLYMORPHISM
--------------------------------------------------------
  ALTER TABLE "SINGLE_NUCLEOTIDE_POLYMORPHISM" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "SINGLE_NUCLEOTIDE_POLYMORPHISM" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table SNP_GENE
--------------------------------------------------------
  ALTER TABLE "SNP_GENE" MODIFY ("GENE_ID" NOT NULL ENABLE);
  ALTER TABLE "SNP_GENE" MODIFY ("SNP_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table SNP_REGION
--------------------------------------------------------
  ALTER TABLE "SNP_REGION" MODIFY ("REGION_ID" NOT NULL ENABLE);
  ALTER TABLE "SNP_REGION" MODIFY ("SNP_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table STUDY
--------------------------------------------------------
  ALTER TABLE "STUDY" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "STUDY" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table STUDY_DISEASE_TRAIT
--------------------------------------------------------
  ALTER TABLE "STUDY_DISEASE_TRAIT" ADD PRIMARY KEY ("STUDY_ID") ENABLE;
  ALTER TABLE "STUDY_DISEASE_TRAIT" MODIFY ("STUDY_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table STUDY_EFO_TRAIT
--------------------------------------------------------
  ALTER TABLE "STUDY_EFO_TRAIT" MODIFY ("EFO_TRAIT_ID" NOT NULL ENABLE);
  ALTER TABLE "STUDY_EFO_TRAIT" MODIFY ("STUDY_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table STUDY_SNP
--------------------------------------------------------
  ALTER TABLE "STUDY_SNP" MODIFY ("SNP_ID" NOT NULL ENABLE);
  ALTER TABLE "STUDY_SNP" MODIFY ("STUDY_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table ASSOCIATION
--------------------------------------------------------
  ALTER TABLE "ASSOCIATION" ADD CONSTRAINT "ASSOC_STUDY_ID_FK" FOREIGN KEY ("STUDY_ID")
	  REFERENCES "STUDY" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ASSOCIATION_EFO_TRAIT
--------------------------------------------------------
  ALTER TABLE "ASSOCIATION_EFO_TRAIT" ADD CONSTRAINT "ASSOC_TRAIT_TRAIT_ID_FK" FOREIGN KEY ("EFO_TRAIT_ID")
	  REFERENCES "EFO_TRAIT" ("ID") ENABLE;
  ALTER TABLE "ASSOCIATION_EFO_TRAIT" ADD CONSTRAINT "ASSOC_TRAIT_ASSOC_ID_FK" FOREIGN KEY ("ASSOCIATION_ID")
	  REFERENCES "ASSOCIATION" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ASSOCIATION_REPORTED_GENE
--------------------------------------------------------
  ALTER TABLE "ASSOCIATION_REPORTED_GENE" ADD CONSTRAINT "ASSOC_REP_GENE_REP_GENE_ID_FK" FOREIGN KEY ("REPORTED_GENE_ID")
	  REFERENCES "GENE" ("ID") ENABLE;
  ALTER TABLE "ASSOCIATION_REPORTED_GENE" ADD CONSTRAINT "ASSOC_REP_GENE_ASSOC_ID_FK" FOREIGN KEY ("ASSOCIATION_ID")
	  REFERENCES "ASSOCIATION" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ASSOCIATION_SNP
--------------------------------------------------------
  ALTER TABLE "ASSOCIATION_SNP" ADD CONSTRAINT "ASSOC_SNP_SNP_ID_FK" FOREIGN KEY ("SNP_ID")
	  REFERENCES "SINGLE_NUCLEOTIDE_POLYMORPHISM" ("ID") ENABLE;
  ALTER TABLE "ASSOCIATION_SNP" ADD CONSTRAINT "ASSOC_SNP_ASSOC_ID_FK" FOREIGN KEY ("ASSOCIATION_ID")
	  REFERENCES "ASSOCIATION" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ETHNICITY
--------------------------------------------------------
  ALTER TABLE "ETHNICITY" ADD CONSTRAINT "ETHNICITY_STUDY_ID_FK" FOREIGN KEY ("STUDY_ID")
	  REFERENCES "STUDY" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table HOUSEKEEPING
--------------------------------------------------------
  ALTER TABLE "HOUSEKEEPING" ADD CONSTRAINT "HOUSEKEEPING_STATUS_ID_FK" FOREIGN KEY ("CURATION_STATUS_ID")
	  REFERENCES "CURATION_STATUS" ("ID") ENABLE;
  ALTER TABLE "HOUSEKEEPING" ADD CONSTRAINT "HOUSEKEEPING_CURATOR_ID_FK" FOREIGN KEY ("CURATOR_ID")
	  REFERENCES "CURATOR" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table SNP_GENE
--------------------------------------------------------
  ALTER TABLE "SNP_GENE" ADD CONSTRAINT "SNP_GENE_GENE_ID_FK" FOREIGN KEY ("GENE_ID")
	  REFERENCES "GENE" ("ID") ENABLE;
  ALTER TABLE "SNP_GENE" ADD CONSTRAINT "SNP_GENE_SNP_ID_FK" FOREIGN KEY ("SNP_ID")
	  REFERENCES "SINGLE_NUCLEOTIDE_POLYMORPHISM" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table SNP_REGION
--------------------------------------------------------
  ALTER TABLE "SNP_REGION" ADD CONSTRAINT "SNP_REGION_SNP_ID_FK" FOREIGN KEY ("SNP_ID")
	  REFERENCES "SINGLE_NUCLEOTIDE_POLYMORPHISM" ("ID") ENABLE;
  ALTER TABLE "SNP_REGION" ADD CONSTRAINT "SNP_REGION_REGION_ID_FK" FOREIGN KEY ("REGION_ID")
	  REFERENCES "REGION" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table STUDY
--------------------------------------------------------
  ALTER TABLE "STUDY" ADD CONSTRAINT "STUDY_HOUSEKEEPING_ID_FK" FOREIGN KEY ("HOUSEKEEPING_ID")
	  REFERENCES "HOUSEKEEPING" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table STUDY_DISEASE_TRAIT
--------------------------------------------------------
  ALTER TABLE "STUDY_DISEASE_TRAIT" ADD CONSTRAINT "STUDY_DISEASE_STUDY_ID_FK" FOREIGN KEY ("STUDY_ID")
	  REFERENCES "STUDY" ("ID") ENABLE;
  ALTER TABLE "STUDY_DISEASE_TRAIT" ADD CONSTRAINT "STUDY_DISEASE_TRAIT_ID_FK" FOREIGN KEY ("DISEASE_TRAIT_ID")
	  REFERENCES "DISEASE_TRAIT" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table STUDY_EFO_TRAIT
--------------------------------------------------------
  ALTER TABLE "STUDY_EFO_TRAIT" ADD CONSTRAINT "STUDY_EFO_TRAIT_ID_FK" FOREIGN KEY ("EFO_TRAIT_ID")
	  REFERENCES "EFO_TRAIT" ("ID") ENABLE;
  ALTER TABLE "STUDY_EFO_TRAIT" ADD CONSTRAINT "STUDY_EFO_STUDY_ID_FK" FOREIGN KEY ("STUDY_ID")
	  REFERENCES "STUDY" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table STUDY_SNP
--------------------------------------------------------

  ALTER TABLE "STUDY_SNP" ADD CONSTRAINT "STUDY_SNP_SNP_ID_FK" FOREIGN KEY ("SNP_ID")
	  REFERENCES "SINGLE_NUCLEOTIDE_POLYMORPHISM" ("ID") ENABLE;
  ALTER TABLE "STUDY_SNP" ADD CONSTRAINT "STUDY_SNP_STUDY_ID_FK" FOREIGN KEY ("STUDY_ID")
	  REFERENCES "STUDY" ("ID") ENABLE;


/*
#################################
#  MIGRATE DATA INTO NEW TABLES #
#################################
*/

--------------------------------------------------------
-- Migrate data into COUNTRY
--------------------------------------------------------
INSERT INTO COUNTRY (ID, MAJOR_AREA, NAME, REGION) SELECT ID, MAJORAREA, COUNTRY, REGION FROM GWASCOUNTRIES;

--------------------------------------------------------
-- Migrate data into GENE
--------------------------------------------------------
INSERT INTO GENE (ID, GENE_NAME) SELECT ID, GENE FROM GWASGENE;

--------------------------------------------------------
-- Migrate data into REGION
--------------------------------------------------------
INSERT INTO REGION (ID, NAME) SELECT ID, REGION FROM GWASREGION;

--------------------------------------------------------
-- Migrate data into SNP
--------------------------------------------------------
-- Insert (clean) migrated data
INSERT INTO SINGLE_NUCLEOTIDE_POLYMORPHISM (ID, CHROMOSOME_NAME, CHROMOSOME_POSITION, RS_ID)
  SELECT ID, CHR_ID, CHR_POS, RS_ID FROM (
  SELECT DISTINCT s.ID, gs.CHR_ID, gs.CHR_POS, s.SNP AS RS_ID
  FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  WHERE s.ID IS NOT NULL AND CHR_ID IS NOT NULL AND CHR_POS IS NOT NULL);
-- Insert remaining, probably messy data: this may or may not be well linked from association data
INSERT INTO SINGLE_NUCLEOTIDE_POLYMORPHISM (ID, CHROMOSOME_NAME, CHROMOSOME_POSITION, RS_ID)
  SELECT DISTINCT s.ID, gs.CHR_ID, gs.CHR_POS, s.SNP AS RS_ID
  FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  LEFT JOIN SINGLE_NUCLEOTIDE_POLYMORPHISM ns ON ns.ID = s.ID
  WHERE NOT (s.ID IS NOT NULL AND CHR_ID IS NOT NULL AND CHR_POS IS NOT NULL)
  AND ns.ID IS NULL;

-- ?? Set LAST_UPDATE_DATE ???

--------------------------------------------------------
-- Migrate data into EF0_TRAIT
--------------------------------------------------------
INSERT INTO EFO_TRAIT (ID, TRAIT, URI) SELECT ID, EFOTRAIT, EFOURI FROM GWASEFOTRAITS;

--------------------------------------------------------
-- Migrate data into CURATOR
--------------------------------------------------------
INSERT INTO CURATOR (ID, FIRST_NAME, LAST_NAME, EMAIL, USER_NAME) SELECT ID, FIRSTNAME, LASTNAME, EMAIL, USERNAME FROM GWASCURATORS;

--------------------------------------------------------
-- Migrate data into CURATION_STATUS
--------------------------------------------------------
INSERT INTO CURATION_STATUS (ID, STATUS, SEQNBR) SELECT ID, STATUS, SEQNBR FROM GWASSTATUS;

--------------------------------------------------------
-- Migrate data into DISEASE_TRAIT
--------------------------------------------------------
INSERT INTO DISEASE_TRAIT (ID, TRAIT) SELECT ID, DISEASETRAIT FROM GWASDISEASETRAITS;

--------------------------------------------------------
-- Migrate data into HOUSEKEEPING
--------------------------------------------------------
INSERT INTO HOUSEKEEPING (
  ID, STUDY_SNP_CHECKED_LEVEL_ONE, STUDY_SNP_CHECKED_LEVEL_TWO, PUBLISH_DATE, NOTES,
  ETHNICITY_CHECKED_LEVEL_ONE, ETHNICITY_CHECKED_LEVEL_TWO, SEND_TONCBIDATE,
  CHECKEDNCBIERROR, FILE_NAME, CURATOR_ID, CURATION_STATUS_ID,
  ETHNICITY_BACK_FILLED, STUDY_ADDED_DATE, LAST_UPDATE_DATE)
SELECT
  ID, CHECKEDHEATHER, CHECKEDLUCIA, PUBLISHDATE, NOTESTEXT,
  ETHNICITYCHECKEDBYJAYAPEGGY, ETHNICITYCHECKEDBYHEATHER, SENDTONCBIDATE,
  CHECKEDNCBIERROR, FILENAM, CURATORID, CURATORSTATUSID,
  ETHNICITYBACKFILLED, STUDYADDEDDATE, LASTUPDATEDATE FROM GWASSTUDIES;
-- this excludes: PUBLISH, PENDING, SENDTONCBI, RECHECKSNPS

--------------------------------------------------------
-- Migrate data into STUDY
--------------------------------------------------------
INSERT INTO STUDY (
  ID, AUTHOR, STUDY_DATE, PUBLICATION, TITLE, INITIAL_SAMPLE_SIZE, REPLICATE_SAMPLE_SIZE, PLATFORM, PUBMED_ID, CNV, GXE, GXG, HOUSEKEEPING_ID)
SELECT
  ID, AUTHOR, STUDYDATE, PUBLICATION, LINKTITLE, INITSAMPLESIZE, REPLICSAMPLESIZE, PLATFORM, PMID, CNV, GXE, GXG, ID FROM GWASSTUDIES;

--------------------------------------------------------
-- Migrate data into ETHNICITY
--------------------------------------------------------
INSERT INTO ETHNICITY (
  ID, STUDY_ID, TYPE, NUMBER_OF_INDIVIDUALS, ETHNIC_GROUP, COUNTRY_OF_ORIGIN, COUNTRY_OF_RECRUITMENT, DESCRIPTION, PREVIOUSLY_REPORTED, SAMPLE_SIZES_MATCH, NOTES)
SELECT
  ID, GWASID, TYPE, NUMINDIVIDUALS, ETHNICGROUP, COUNTRYORIGIN, COUNTRYRECRUITMENT, ADDLDESCRIPTION, PREVIOUSLYREPORTED, SAMPLESIZESMATCH, NOTES FROM GWASETHNICITY WHERE ID != 7032 AND ID != 7033;
-- Excluding two records that reference missing study 5231

--------------------------------------------------------
-- Migrate data into ASSOCIATION
--------------------------------------------------------
-- Insert migrated data
INSERT INTO ASSOCIATION (
  ID, STUDY_ID, STRONGEST_ALLELE, RISK_FREQUENCY, ALLELE, SNP_TYPE,
  PVALUE_FLOAT, PVALUE_TEXT, PVALUE_MANTISSA, PVALUE_EXPONENT,
  OR_PER_COPY_RECIP, OR_PER_COPY_STD_ERROR, OR_PER_COPY_RANGE, OR_PER_COPY_UNIT_DESCR, OR_PER_COPY_NUM, OR_TYPE)
SELECT
  ID, GWASID, STRONGESTALLELE, RISKFREQUENCY, ALLELE, SNPTYPE,
  PVALUEFLOAT, PVALUETXT, PVALUE_MANTISSA, PVALUE_EXPONENT,
  ORPERCOPYRECIP, ORPERCOPYSTDERROR, ORPERCOPYRANGE, ORPERCOPYUNITDESCR, ORPERCOPYNUM, ORTYPE FROM GWASSTUDIESSNP;

--------------------------------------------------------
-- Migrate refs into ASSOCIATION_SNP
--------------------------------------------------------
INSERT INTO ASSOCIATION_SNP (ASSOCIATION_ID, SNP_ID)
SELECT DISTINCT gs.ID AS ASSOCIATION_ID, s.ID AS SNP_ID FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  WHERE s.ID IS NOT NULL
  AND CHR_ID IS NOT NULL
  AND CHR_POS IS NOT NULL;

--------------------------------------------------------
-- Migrate refs into SNP_GENE
--------------------------------------------------------
INSERT INTO SNP_GENE (SNP_ID, GENE_ID)
SELECT s.ID AS SNP_ID, g.ID AS GENE_ID FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  JOIN GWASGENEXREF gx ON gx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASGENE g ON gx.GENEID = g.ID;

--------------------------------------------------------
-- Migrate refs into STUDY_SNP
--------------------------------------------------------
INSERT INTO STUDY_SNP (STUDY_ID, SNP_ID)
SELECT st.ID AS STUDY_ID, snp.ID AS SNP_ID
  FROM GWASSTUDIES st
  JOIN GWASSTUDIESSNP x ON x.GWASID = st.ID
  JOIN GWASSNPXREF snpx ON snpx.GWASSTUDIESSNPID = x.ID
  JOIN GWASSNP snp ON snp.ID = snpx.SNPID;

--------------------------------------------------------
-- Migrate refs into STUDY_DISEASE_TRAIT
--------------------------------------------------------
INSERT INTO STUDY_DISEASE_TRAIT (STUDY_ID, DISEASE_TRAIT_ID)
SELECT s.ID AS STUDY_ID, d.ID AS DISEASE_TRAIT_ID
  FROM GWASSTUDIES s
  JOIN GWASDISEASETRAITS d ON d.ID = s.DISEASEID;


/*
###################################
#  CREATE SEQUENCES and TRIGGERS  #
###################################
*/

--------------------------------------------------------
-- Create HIBERNATE_SEQUENCE
--------------------------------------------------------
CREATE SEQUENCE HIBERNATE_SEQUENCE MINVALUE 10000000 MAXVALUE 9999999999999999999999999999 START WITH 10000000 INCREMENT BY 1 NOCYCLE NOORDER CACHE 20;


--------------------------------------------------------
-- Create trigger on COUNTRY
--------------------------------------------------------

CREATE OR REPLACE TRIGGER COUNTRY_TRG
BEFORE INSERT ON COUNTRY
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER COUNTRY_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on GENE
--------------------------------------------------------
CREATE OR REPLACE TRIGGER GENE_TRG
BEFORE INSERT ON GENE
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER GENE_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on REGION
--------------------------------------------------------
CREATE OR REPLACE TRIGGER REGION_TRG
BEFORE INSERT ON REGION
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER REGION_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on SNP
--------------------------------------------------------
CREATE OR REPLACE TRIGGER SNP_TRG
BEFORE INSERT ON SINGLE_NUCLEOTIDE_POLYMORPHISM
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER SNP_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on EFOTRAIT
--------------------------------------------------------
CREATE OR REPLACE TRIGGER EFO_TRAIT_TRG
BEFORE INSERT ON EFO_TRAIT
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER EFO_TRAIT_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on CURATOR
--------------------------------------------------------
CREATE OR REPLACE TRIGGER CURATOR_TRG
BEFORE INSERT ON CURATOR
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER CURATOR_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on CURATION_STATUS
--------------------------------------------------------
CREATE OR REPLACE TRIGGER CURATION_STATUS_TRG
BEFORE INSERT ON CURATION_STATUS
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER CURATION_STATUS_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on DISEASE_TRAIT
--------------------------------------------------------
CREATE OR REPLACE TRIGGER DISEASE_TRAIT_TRG
BEFORE INSERT ON DISEASE_TRAIT
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER DISEASE_TRAIT_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on HOUSEKEEPING
--------------------------------------------------------
CREATE OR REPLACE TRIGGER HOUSEKEEPING_TRG
BEFORE INSERT ON HOUSEKEEPING
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER HOUSEKEEPING_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on STUDY
--------------------------------------------------------
CREATE OR REPLACE TRIGGER STUDY_TRG
BEFORE INSERT ON STUDY
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER STUDY_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on ETHNICITY
--------------------------------------------------------
CREATE OR REPLACE TRIGGER ETHNICITY_TRG
BEFORE INSERT ON ETHNICITY
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER ETHNICITY_TRG ENABLE;

--------------------------------------------------------
-- Create trigger on ASSOCIATION
--------------------------------------------------------
CREATE OR REPLACE TRIGGER ASSOCIATION_TRG
BEFORE INSERT ON ASSOCIATION
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER ASSOCIATION_TRG ENABLE;


/*
##########################################
#  DROP ALL TABLES FROM OLD GWAS SCHEMA  #
##########################################
*/

--DROP TABLE GWASDISEASECATEGORIES;
--DROP TABLE T_GWASSTUDIES;
--DROP TABLE GWASNCBIANNOTATED;
--DROP TABLE GWASCOUNTRIES;
--ALTER TABLE GWASGENEXREF DROP CONSTRAINT GWASGENEXREF_GWASGENE_FK;
--DROP TABLE GWASGENE;
--ALTER TABLE GWASREGIONXREF DROP CONSTRAINT GWASREGIONXREF_GWASREGION_FK;
--DROP TABLE GWASREGION;

