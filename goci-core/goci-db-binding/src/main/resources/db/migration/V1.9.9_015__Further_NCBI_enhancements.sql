/*

################################################################################
Migration script to tweak tables used to contain NCBI reported information,
generated in V1.9.9.014.

Designed for execution with Flyway database migrations tool.

author:  Tony Burdett
date:    February 3rd 2015
version: 1.9.9.015 (pre 2.0)
################################################################################

*/


--------------------------------------------------------
-- Remove SNP_MAPPED_GENE
--------------------------------------------------------

DROP TABLE SNP_MAPPED_GENE;

--------------------------------------------------------
-- Recreate as GENOMIC_CONTEXT
--------------------------------------------------------

CREATE TABLE "GENOMIC_CONTEXT" (
  ID NUMBER (19,0),
  SNP_ID NUMBER(19,0),
  GENE_ID NUMBER(19,0),
  IS_INTERGENIC NUMBER(1,0),
  IS_UPSTREAM NUMBER(1,0),
  IS_DOWNSTREAM NUMBER(1,0),
  DISTANCE NUMBER(19,0));

--------------------------------------------------------
--  Ref Constraints for Table GENOMIC_CONTEXT
--------------------------------------------------------
  ALTER TABLE "GENOMIC_CONTEXT" ADD CONSTRAINT "GC_SNP_ID_FK" FOREIGN KEY ("SNP_ID")
	  REFERENCES "SINGLE_NUCLEOTIDE_POLYMORPHISM" ("ID") ENABLE;
  ALTER TABLE "GENOMIC_CONTEXT" ADD CONSTRAINT "GC_GENE_ID_FK" FOREIGN KEY ("GENE_ID")
	  REFERENCES "GENE" ("ID") ENABLE;


--------------------------------------------------------
-- Create temporary GENOMIC_CONTEXT_SEQUENCE
--------------------------------------------------------
CREATE SEQUENCE GC_SEQUENCE MINVALUE 1 MAXVALUE 9999999 START WITH 1 INCREMENT BY 1 NOCYCLE NOORDER CACHE 20;

--------------------------------------------------------
-- Create temporary trigger on GENOMIC_CONTEXT
--------------------------------------------------------
CREATE OR REPLACE TRIGGER GENOMIC_CONTEXT_TRG
BEFORE INSERT ON GENOMIC_CONTEXT
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT GC_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER GENOMIC_CONTEXT_TRG ENABLE;

--------------------------------------------------------
-- Re-migrate data into GENOMIC_CONTEXT
--------------------------------------------------------

-- UPSTREAM
INSERT INTO GENOMIC_CONTEXT (SNP_ID, GENE_ID, IS_INTERGENIC, IS_UPSTREAM, IS_DOWNSTREAM, DISTANCE)
  SELECT DISTINCT s.ID AS SNP_ID, g.ID AS GENE_ID, 0 AS IS_INTERGENIC, 1 AS IS_UPSTREAM, 0 AS IS_DOWNSTREAM, gs.UPSTREAM_GENE_DISTANCE AS DISTANCE
  FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  JOIN GWASGENE g ON g.GENE = gs.UPSTREAM_GENE_SYMBOL
  WHERE UPSTREAM_GENE_DISTANCE IS NOT NULL
  AND INTERGENIC = 1;
-- DOWNSTREAM
INSERT INTO GENOMIC_CONTEXT (SNP_ID, GENE_ID, IS_INTERGENIC, IS_UPSTREAM, IS_DOWNSTREAM, DISTANCE)
  SELECT DISTINCT s.ID AS SNP_ID, g.ID AS GENE_ID, 0 AS IS_INTERGENIC, 0 AS IS_UPSTREAM, 1 AS IS_DOWNSTREAM, gs.DOWNSTREAM_GENE_DISTANCE AS DISTANCE
  FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  JOIN GWASGENE g ON g.GENE = gs.DOWNSTREAM_GENE_SYMBOL
  WHERE DOWNSTREAM_GENE_DISTANCE IS NOT NULL
  AND INTERGENIC = 1;
-- INTERGENIC
INSERT INTO GENOMIC_CONTEXT (SNP_ID, GENE_ID, IS_INTERGENIC, IS_UPSTREAM, IS_DOWNSTREAM, DISTANCE)
  SELECT DISTINCT s.ID AS SNP_ID, g.ID AS GENE_ID, 1 AS IS_INTERGENIC, 0 AS IS_UPSTREAM, 0 AS IS_DOWNSTREAM, NULL AS DISTANCE
  FROM GWASSTUDIESSNP gs
  JOIN GWASSNPXREF sx ON sx.GWASSTUDIESSNPID = gs.ID
  JOIN GWASSNP s ON sx.SNPID = s.ID
  JOIN GWASGENE g ON g.GENE = gs.SNP_GENE_SYMBOLS
  WHERE INTERGENIC = 0;

--------------------------------------------------------
-- Create proper trigger on GENOMIC_CONTEXT
--------------------------------------------------------
CREATE OR REPLACE TRIGGER GENOMIC_CONTEXT_TRG
BEFORE INSERT ON GENOMIC_CONTEXT
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER GENOMIC_CONTEXT_TRG ENABLE;

--------------------------------------------------------
-- Drop temporary GENOMIC_CONTEXT_SEQUENCE
--------------------------------------------------------
DROP SEQUENCE GC_SEQUENCE;
