/*

################################################################################
Move entrez gene IDs from the current location in the genomic context into
the gene table directly

Designed for execution with Flyway database migrations tool.

author:  Tony Burdett
date:    February 19rd 2015
version: 1.9.9.028 (pre 2.0)
################################################################################

*/

--------------------------------------------------------
--  Mods for Table GENE and GENOMIC_CONTEXT
--------------------------------------------------------

ALTER TABLE GENE ADD (ENTREZ_GENE_ID VARCHAR2(255 CHAR));

-- context 6065 cites Entrez 3107;101929772 for Gene ID 4389
UPDATE GENOMIC_CONTEXT SET ENTREZ_GENE_ID = '3107' WHERE GENE_ID = '4389';
-- context 11606 cites Entrez 3107;101929772 for Gene ID 26339
UPDATE GENOMIC_CONTEXT SET ENTREZ_GENE_ID = '3107' WHERE GENE_ID = '26339';
-- context 6064 cites Entrez 3107;101929772 for Gene ID 28028
UPDATE GENOMIC_CONTEXT SET ENTREZ_GENE_ID = '3107' WHERE GENE_ID = '28028';
-- context 10055422 cites Entrez 26220;100996369 for Gene ID 10044829
UPDATE GENOMIC_CONTEXT SET ENTREZ_GENE_ID = '26220' WHERE GENE_ID = '10044829';
-- context 10056706 cites Entrez 401247;100996707 for Gene ID 4389
UPDATE GENOMIC_CONTEXT SET ENTREZ_GENE_ID = '401247' WHERE GENE_ID = '10044967';


MERGE INTO GENE g
    USING (SELECT DISTINCT ID, ENTREZ_GENE_ID FROM GENOMIC_CONTEXT) gc
    ON (g.ID = gc.GENE_ID)
    WHEN MATCHED THEN UPDATE SET g.ENTREZ_GENE_ID = gc.ENTREZ_GENE_ID;

ALTER TABLE GENOMIC_CONTEXT DROP COLUMN ENTREZ_GENE_ID;
