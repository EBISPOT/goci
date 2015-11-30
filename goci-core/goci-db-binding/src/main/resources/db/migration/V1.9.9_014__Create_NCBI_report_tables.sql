/*

################################################################################
Migration script to create tables used to contain NCBI reported information,
concerning study Pubmed checks, genomic information for SNPs, and other
validations.

Designed for execution with Flyway database migrations tool.

author:  Tony Burdett
date:    February 3rd 2015
version: 1.9.9.014 (pre 2.0)
################################################################################

*/


/*
#######################################
#  CREATE NEW TABLES AND CONSTRAINTS  #
#######################################
*/


--------------------------------------------------------
--  DDL for Table STUDY_REPORT
--------------------------------------------------------

CREATE TABLE "STUDY_REPORT" (
  STUDY_ID NUMBER(19,0),
  PUBMED_ID_ERROR NUMBER(19,0),
  NCBI_PAPER_TITLE VARCHAR2(4000 BYTE),
  NCBI_FIRST_AUTHOR VARCHAR2(255 CHAR),
  NCBI_NORMALIZED_FIRST_AUTHOR VARCHAR2(255 CHAR),
  NCBI_FIRST_UPDATE_DATE DATE);

--------------------------------------------------------
--  DDL for Table SNP_MAPPED_GENE
--------------------------------------------------------

CREATE TABLE "SNP_MAPPED_GENE" (
  SNP_ID NUMBER(19,0),
  GENE_ID NUMBER(19,0),
  IS_INTERGENIC NUMBER(1,0),
  IS_UPSTREAM NUMBER(1,0),
  IS_DOWNSTREAM NUMBER(1,0),
  DISTANCE NUMBER(19,0));

--------------------------------------------------------
--  DDL for Table ASSOCIATION_REPORT
--------------------------------------------------------

CREATE TABLE ASSOCIATION_REPORT (
  ASSOCIATION_ID NUMBER(19,0),
  SNP_PENDING NUMBER(1,0),
  LAST_UPDATE_DATE TIMESTAMP(6),
  GENE_ERROR NUMBER(19,0),
  PUBMED_ID_ERROR NUMBER(19,0),
  SNP_ERROR VARCHAR2(4000 BYTE),
  SNP_GENE_ON_DIFF_CHR VARCHAR2(4000 BYTE),
  NO_GENE_FOR_SYMBOL VARCHAR2(4000 BYTE),
  GENE_NOT_ON_GENOME VARCHAR2(4000 BYTE));

--------------------------------------------------------
--  DDL for Index STUDY_REP_ID_PK
--------------------------------------------------------

CREATE UNIQUE INDEX "STUDY_REP_ID_PK" ON "STUDY_REPORT" ("STUDY_ID");

--------------------------------------------------------
--  DDL for Index ASSOC_REP_ID_PK
--------------------------------------------------------

CREATE UNIQUE INDEX "ASSOC_REP_ID_PK" ON "ASSOCIATION_REPORT" ("ASSOCIATION_ID");

--------------------------------------------------------
--  Constraints for Table STUDY_REPORT
--------------------------------------------------------
  ALTER TABLE "STUDY_REPORT" MODIFY ("STUDY_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Constraints for Table ASSOCIATION_REPORT
--------------------------------------------------------
  ALTER TABLE "ASSOCIATION_REPORT" MODIFY ("ASSOCIATION_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table STUDY_REPORT
--------------------------------------------------------
  ALTER TABLE "STUDY_REPORT" ADD CONSTRAINT "STUDY_REP_STUDY_ID_FK" FOREIGN KEY ("STUDY_ID")
	  REFERENCES "STUDY" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table SNP_MAPPED_GENE
--------------------------------------------------------
  ALTER TABLE "SNP_MAPPED_GENE" ADD CONSTRAINT "SNP_MAP_GENE_SNP_ID_FK" FOREIGN KEY ("SNP_ID")
	  REFERENCES "SINGLE_NUCLEOTIDE_POLYMORPHISM" ("ID") ENABLE;
  ALTER TABLE "SNP_MAPPED_GENE" ADD CONSTRAINT "SNP_MAP_GENE_GENE_ID_FK" FOREIGN KEY ("GENE_ID")
	  REFERENCES "GENE" ("ID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ASSOCIATION_REPORT
--------------------------------------------------------
  ALTER TABLE "ASSOCIATION_REPORT" ADD CONSTRAINT "ASSOC_REP_ASSOC_ID_FK" FOREIGN KEY ("ASSOCIATION_ID")
	  REFERENCES "ASSOCIATION" ("ID") ENABLE;

--------------------------------------------------------
--  Mods for Table SINGLE_NUCLEOTIDE_POLYMORPHISM
--------------------------------------------------------

ALTER TABLE SINGLE_NUCLEOTIDE_POLYMORPHISM ADD (
  MERGED NUMBER(19,0),
  FUNCTIONAL_CLASS VARCHAR2(255 CHAR));


/*
#################################
#  MIGRATE DATA INTO NEW TABLES #
#################################
*/


--------------------------------------------------------
-- Migrate data into STUDY_REPORT
--------------------------------------------------------

INSERT INTO STUDY_REPORT (STUDY_ID, NCBI_PAPER_TITLE, PUBMED_ID_ERROR, NCBI_FIRST_AUTHOR, NCBI_NORMALIZED_FIRST_AUTHOR, NCBI_FIRST_UPDATE_DATE)
  SELECT ID, NCBIPAPER_TITLE, PUBMDID_ERROR, NCBIAUTHOR_FIRST, NCBIAUTHOR_FIRST_NORMALIZED, NCBIFIRSTUPDATEDATE FROM GWASSTUDIES;

--------------------------------------------------------
-- Migrate data into SNP_MAPPED_GENE
--------------------------------------------------------

-- UPSTREAM
INSERT INTO SNP_MAPPED_GENE (SNP_ID, GENE_ID, IS_INTERGENIC, IS_UPSTREAM, IS_DOWNSTREAM, DISTANCE)
  SELECT s.ID AS SNP_ID, g.ID AS GENE_ID, 0 AS IS_INTERGENIC, 1 AS IS_UPSTREAM, 0 AS IS_DOWNSTREAM, gs.UPSTREAM_GENE_DISTANCE AS DISTANCE
  FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  JOIN GWASGENE g ON g.GENE = gs.UPSTREAM_GENE_SYMBOL
  WHERE UPSTREAM_GENE_DISTANCE IS NOT NULL
  AND INTERGENIC = 1;
-- DOWNSTREAM
INSERT INTO SNP_MAPPED_GENE (SNP_ID, GENE_ID, IS_INTERGENIC, IS_UPSTREAM, IS_DOWNSTREAM, DISTANCE)
  SELECT s.ID AS SNP_ID, g.ID AS GENE_ID, 0 AS IS_INTERGENIC, 0 AS IS_UPSTREAM, 1 AS IS_DOWNSTREAM, gs.DOWNSTREAM_GENE_DISTANCE AS DISTANCE
  FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  JOIN GWASGENE g ON g.GENE = gs.DOWNSTREAM_GENE_SYMBOL
  WHERE DOWNSTREAM_GENE_DISTANCE IS NOT NULL
  AND INTERGENIC = 1;
-- INTERGENIC
INSERT INTO SNP_MAPPED_GENE (SNP_ID, GENE_ID, IS_INTERGENIC, IS_UPSTREAM, IS_DOWNSTREAM, DISTANCE)
  SELECT s.ID AS SNP_ID, g.ID AS GENE_ID, 1 AS IS_INTERGENIC, 0 AS IS_UPSTREAM, 0 AS IS_DOWNSTREAM, NULL AS DISTANCE
  FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  JOIN GWASGENE g ON g.GENE = gs.SNP_GENE_SYMBOLS
  WHERE INTERGENIC = 0;

--------------------------------------------------------
-- Migrate data into ASSOCIATION_REPORT
--------------------------------------------------------

INSERT INTO ASSOCIATION_REPORT (ASSOCIATION_ID, SNP_PENDING, LAST_UPDATE_DATE, GENE_ERROR, PUBMED_ID_ERROR, SNP_ERROR, SNP_GENE_ON_DIFF_CHR, NO_GENE_FOR_SYMBOL, GENE_NOT_ON_GENOME)
  SELECT ID, SNPPENDING, LASTUPDATEDATE, GENE_ERROR, PUBMDID_ERROR, SNP_ID_ERROR, SNP_GENE_ON_DIFF_CHR, NO_GENEID_FOR_SYMBOL, GENE_NOT_ON_GENOME FROM GWASSTUDIESSNP;

--------------------------------------------------------
-- Migrate data into SINGLE_NUCLEOTIDE_POLYMORPHISM
--------------------------------------------------------

UPDATE SINGLE_NUCLEOTIDE_POLYMORPHISM snp SET (MERGED, FUNCTIONAL_CLASS) = (
SELECT DISTINCT gs.MERGED, gs.FUNCTIONAL_CLASS
  FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  WHERE MERGED IS NOT NULL
  AND s.ID = snp.ID);
