/*

################################################################################
Migration script to adapt additional data that have undergone some more complex
modelling changes from NHGRI GWAS database dump into revised EBI schema
(this info wasn't included in 003)

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Tony Burdett
date:    January 21st 2015
version: 1.9.9.004 (pre 2.0)
################################################################################

*/

/*
#############################################
#  POPULATE JOIN TABLES FROM EXISTING DATA  #
#############################################
*/


--------------------------------------------------------
--  Links for Table ASSOCIATION_REPORTED_GENE
--------------------------------------------------------

INSERT INTO ASSOCIATION_REPORTED_GENE (ASSOCIATION_ID, REPORTED_GENE_ID)
SELECT a.ID AS ASSOCIATION_ID, g.ID AS GENE_ID FROM (
  SELECT * FROM (
    SELECT DISTINCT ID, TRIM(REGEXP_SUBSTR(GENE, '[^,]+', 1, LEVEL)) GENE FROM (
      SELECT * FROM (
        SELECT DISTINCT ID, TRIM(GENE) AS GENE
        FROM GWASSTUDIESSNP
      )
      WHERE LOWER(GENE) != 'intergenic'
      AND LOWER(GENE) != 'nr'
    )
    CONNECT BY INSTR(GENE, ',', 1, LEVEL - 1 ) > 0
  )
) a
JOIN GWASGENE g ON TRIM(g.GENE) = a.GENE;

--------------------------------------------------------
--  Links for Table SNP_REGION
--------------------------------------------------------



