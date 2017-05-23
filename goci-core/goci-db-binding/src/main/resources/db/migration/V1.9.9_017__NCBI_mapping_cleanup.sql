/*

################################################################################
Migration script to flip intra/intergenic naming error introduced in V1.9.9.014

Designed for execution with Flyway database migrations tool.

author:  Tony Burdett
date:    February 6rd 2015
version: 1.9.9.017 (pre 2.0)
################################################################################

*/


--------------------------------------------------------
-- Remove SNP_GENE
--------------------------------------------------------

DROP TABLE SNP_GENE;

--------------------------------------------------------
-- Modify GENOMIC_CONTEXT - flip wrong inter/intragenic
--------------------------------------------------------

ALTER TABLE "GENOMIC_CONTEXT" RENAME COLUMN IS_INTERGENIC TO IS_INTRAGENIC;
ALTER TABLE "GENOMIC_CONTEXT" ADD ("IS_INTERGENIC" NUMBER(1,0));

UPDATE "GENOMIC_CONTEXT" SET IS_INTERGENIC = '1' WHERE IS_INTRAGENIC = '0';
UPDATE "GENOMIC_CONTEXT" SET IS_INTERGENIC = '0' WHERE IS_INTRAGENIC = '1';

ALTER TABLE "GENOMIC_CONTEXT" DROP COLUMN IS_INTRAGENIC;


