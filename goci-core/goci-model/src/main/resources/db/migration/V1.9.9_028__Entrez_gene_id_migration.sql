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

-- where context cites Entrez 3107;101929772, update to 3107
UPDATE GENOMIC_CONTEXT SET ENTREZ_GENE_ID = '3107' WHERE ENTREZ_GENE_ID = '3107;101929772';
-- where context cites Entrez 26220;100996369, update to 26220
UPDATE GENOMIC_CONTEXT SET ENTREZ_GENE_ID = '26220' WHERE ENTREZ_GENE_ID = '26220;100996369';
-- where context cites Entrez 401247;100996707, update to 401247
UPDATE GENOMIC_CONTEXT SET ENTREZ_GENE_ID = '401247' WHERE ENTREZ_GENE_ID = '401247;100996707';
