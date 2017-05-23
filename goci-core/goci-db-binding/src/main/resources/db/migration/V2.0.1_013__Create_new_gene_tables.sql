/*
################################################################################
Migration script to create ENTREZ_GENE and ENSEMBL_GENE table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    July 21th 2015
version: 2.0.1.013
################################################################################

#######################################
#  CREATE NEW TABLES AND CONSTRAINTS
#######################################
*/
--------------------------------------------------------
--  Create Table ENSEMBL_GENE
--------------------------------------------------------

  CREATE TABLE "ENSEMBL_GENE" (
     "ID" NUMBER(19,0),
     "ENSEMBL_GENE_ID" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Index ENSEMBL_GENE_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "ENSEMBL_GENE_ID_PK" ON "ENSEMBL_GENE" ("ID");

--------------------------------------------------------
--  Constraints for Table ENSEMBL_GENE
--------------------------------------------------------
  ALTER TABLE "ENSEMBL_GENE" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "ENSEMBL_GENE" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
-- Create trigger on LOCATION
--------------------------------------------------------

CREATE OR REPLACE TRIGGER ENSEMBL_GENE_TRG
BEFORE INSERT ON "ENSEMBL_GENE"
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER ENSEMBL_GENE_TRG ENABLE;


--------------------------------------------------------
--  Create Table ENTREZ_GENE
--------------------------------------------------------

  CREATE TABLE "ENTREZ_GENE" (
     "ID" NUMBER(19,0),
     "ENTREZ_GENE_ID" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Index ENTREZ_GENE_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "ENTREZ_GENE_ID_PK" ON "ENTREZ_GENE" ("ID");

--------------------------------------------------------
--  Constraints for Table ENTREZ_GENE
--------------------------------------------------------
  ALTER TABLE "ENTREZ_GENE" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "ENTREZ_GENE" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
-- Create trigger on LOCATION
--------------------------------------------------------

CREATE OR REPLACE TRIGGER ENTREZ_GENE_TRG
BEFORE INSERT ON "ENTREZ_GENE"
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER ENTREZ_GENE_TRG ENABLE;