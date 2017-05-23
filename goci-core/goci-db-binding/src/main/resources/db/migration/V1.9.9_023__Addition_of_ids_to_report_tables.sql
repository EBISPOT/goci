/*

################################################################################
Modify the study report and association report tables to include an ID column
for use in updating/deleting in response to study/association changes

Designed for execution with Flyway database migrations tool.

author:  Tony Burdett
date:    February 12th 2015
version: 1.9.9.023 (pre 2.0)
################################################################################

*/

--------------------------------------------------------
--  Drop original report tables
--------------------------------------------------------

DROP TABLE STUDY_REPORT;
DROP TABLE ASSOCIATION_REPORT;


--------------------------------------------------------
--  DDL for Table STUDY_REPORT
--------------------------------------------------------

CREATE TABLE "STUDY_REPORT" (
  ID NUMBER(19,0),
  STUDY_ID NUMBER(19,0),
  PUBMED_ID_ERROR NUMBER(19,0),
  NCBI_PAPER_TITLE VARCHAR2(4000 BYTE),
  NCBI_FIRST_AUTHOR VARCHAR2(255 CHAR),
  NCBI_NORMALIZED_FIRST_AUTHOR VARCHAR2(255 CHAR),
  NCBI_FIRST_UPDATE_DATE DATE);

--------------------------------------------------------
--  DDL for Table ASSOCIATION_REPORT
--------------------------------------------------------

CREATE TABLE ASSOCIATION_REPORT (
  ID NUMBER(19,0),
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

CREATE UNIQUE INDEX "STUDY_REP_ID_PK" ON "STUDY_REPORT" ("ID");

--------------------------------------------------------
--  DDL for Index ASSOC_REP_ID_PK
--------------------------------------------------------

CREATE UNIQUE INDEX "ASSOC_REP_ID_PK" ON "ASSOCIATION_REPORT" ("ID");

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
--  Ref Constraints for Table ASSOCIATION_REPORT
--------------------------------------------------------
  ALTER TABLE "ASSOCIATION_REPORT" ADD CONSTRAINT "ASSOC_REP_ASSOC_ID_FK" FOREIGN KEY ("ASSOCIATION_ID")
	  REFERENCES "ASSOCIATION" ("ID") ENABLE;

--------------------------------------------------------
-- Create temporary STUDY_REPORT SEQUENCE
--------------------------------------------------------
CREATE SEQUENCE SRN_SEQUENCE MINVALUE 1 MAXVALUE 9999999 START WITH 1 INCREMENT BY 1 NOCYCLE NOORDER CACHE 20;

--------------------------------------------------------
-- Create temporary ASSOCIATION_REPORT SEQUENCE
--------------------------------------------------------
CREATE SEQUENCE ARN_SEQUENCE MINVALUE 1 MAXVALUE 9999999 START WITH 1 INCREMENT BY 1 NOCYCLE NOORDER CACHE 20;

--------------------------------------------------------
-- Create temporary trigger on STUDY_REPORT_NEW
--------------------------------------------------------
CREATE OR REPLACE TRIGGER SRN_TRG
BEFORE INSERT ON STUDY_REPORT
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT SRN_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER SRN_TRG ENABLE;

--------------------------------------------------------
-- Create temporary trigger on ASSOCIATION_REPORT_NEW
--------------------------------------------------------
CREATE OR REPLACE TRIGGER ARN_TRG
BEFORE INSERT ON ASSOCIATION_REPORT
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT ARN_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER ARN_TRG ENABLE;

--------------------------------------------------------
-- Re-migrate data into new STUDY_REPORT
--------------------------------------------------------

INSERT INTO STUDY_REPORT (STUDY_ID, NCBI_PAPER_TITLE, PUBMED_ID_ERROR, NCBI_FIRST_AUTHOR, NCBI_NORMALIZED_FIRST_AUTHOR, NCBI_FIRST_UPDATE_DATE)
  SELECT ID, NCBIPAPER_TITLE, PUBMDID_ERROR, NCBIAUTHOR_FIRST, NCBIAUTHOR_FIRST_NORMALIZED, NCBIFIRSTUPDATEDATE FROM GWASSTUDIES;

--------------------------------------------------------
-- Migrate data into new ASSOCIATION_REPORT
--------------------------------------------------------

INSERT INTO ASSOCIATION_REPORT (ASSOCIATION_ID, SNP_PENDING, LAST_UPDATE_DATE, GENE_ERROR, PUBMED_ID_ERROR, SNP_ERROR, SNP_GENE_ON_DIFF_CHR, NO_GENE_FOR_SYMBOL, GENE_NOT_ON_GENOME)
  SELECT ID, SNPPENDING, LASTUPDATEDATE, GENE_ERROR, PUBMDID_ERROR, SNP_ID_ERROR, SNP_GENE_ON_DIFF_CHR, NO_GENEID_FOR_SYMBOL, GENE_NOT_ON_GENOME FROM GWASSTUDIESSNP;

--------------------------------------------------------
-- Drop temporary Triggers and Sequences
--------------------------------------------------------

DROP SEQUENCE SRN_SEQUENCE;
DROP SEQUENCE ARN_SEQUENCE;

DROP TRIGGER SRN_TRG;
DROP TRIGGER ARN_TRG;

--------------------------------------------------------
-- Create proper triggers on Reports
--------------------------------------------------------

CREATE OR REPLACE TRIGGER STUDY_REPORT_TRG
BEFORE INSERT ON STUDY_REPORT
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER STUDY_REPORT_TRG ENABLE;

CREATE OR REPLACE TRIGGER ASSOCIATION_REPORT_TRG
BEFORE INSERT ON ASSOCIATION_REPORT
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER ASSOCIATION_REPORT_TRG ENABLE;


