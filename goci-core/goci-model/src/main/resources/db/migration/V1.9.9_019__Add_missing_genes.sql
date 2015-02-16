/*

################################################################################
Create a view table that inserts any missing genes into the gene table,
determined by inspecting upstream, downstream and intragenic mapped gene symbols
from the NCBI pipeline

Designed for execution with Flyway database migrations tool.

author:  Tony Burdett
date:    February 6rd 2015
version: 1.9.9.019 (pre 2.0)
################################################################################

*/

--------------------------------------------------------
-- Migrate data from GWASSTUDIESSNP if missing GENE
--------------------------------------------------------
-- UPSTREAM
INSERT INTO GENE (GENE_NAME)
  SELECT DISTINCT gs.UPSTREAM_GENE_SYMBOL FROM GWASSTUDIESSNP gs
  LEFT JOIN GENE g ON g.GENE_NAME = gs.UPSTREAM_GENE_SYMBOL
  WHERE gs.UPSTREAM_GENE_SYMBOL IS NOT NULL
  AND g.GENE_NAME IS NULL;
-- DOWNSTREAM
INSERT INTO GENE (GENE_NAME)
  SELECT DISTINCT gs.DOWNSTREAM_GENE_SYMBOL FROM GWASSTUDIESSNP gs
  LEFT JOIN GENE g ON g.GENE_NAME = gs.DOWNSTREAM_GENE_SYMBOL
  WHERE gs.DOWNSTREAM_GENE_SYMBOL IS NOT NULL
  AND g.GENE_NAME IS NULL;
-- INTRAGENIC
INSERT INTO GENE (GENE_NAME)
  SELECT DISTINCT gs.SNP_GENE_SYMBOLS FROM GWASSTUDIESSNP gs
  LEFT JOIN GENE g ON g.GENE_NAME = gs.SNP_GENE_SYMBOLS
  WHERE gs.SNP_GENE_SYMBOLS IS NOT NULL
  AND g.GENE_NAME IS NULL
  AND gs.SNP_GENE_IDS NOT LIKE '%;%';
