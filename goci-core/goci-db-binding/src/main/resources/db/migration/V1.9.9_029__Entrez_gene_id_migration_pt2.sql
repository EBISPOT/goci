/*

################################################################################
Retrospective fix to ensure merging happens after genomic context cleanup.

Designed for execution with Flyway database migrations tool.

author:  Tony Burdett
date:    March 9th 2015
version: 1.9.9.029 (pre 2.0)
################################################################################

*/

--------------------------------------------------------
--  Mods for Table GENE and GENOMIC_CONTEXT
--------------------------------------------------------

MERGE INTO GENE g
    USING (SELECT DISTINCT GENE_ID, ENTREZ_GENE_ID FROM GENOMIC_CONTEXT) gc
    ON (g.ID = gc.GENE_ID)
    WHEN MATCHED THEN UPDATE SET g.ENTREZ_GENE_ID = gc.ENTREZ_GENE_ID;

ALTER TABLE GENOMIC_CONTEXT DROP COLUMN ENTREZ_GENE_ID;
