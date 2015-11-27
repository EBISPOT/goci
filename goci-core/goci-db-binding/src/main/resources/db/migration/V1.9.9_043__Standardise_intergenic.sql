/*

################################################################################
Migration script to remove duplicate entries for 'intergenic' in the gene table
and use only one single one

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:    May 21st 2015
version: 1.9.9.043 (pre 2.0)
################################################################################

--------------------------------------------------------
--  STANDARDISE INTERGENIC ENTRIES IN GENE TABLE
--------------------------------------------------------
*/


    UPDATE AUTHOR_REPORTED_GENE SET REPORTED_GENE_ID = 11627 WHERE REPORTED_GENE_ID IN (SELECT ID FROM GENE WHERE GENE_NAME LIKE '%genic%');

    DELETE FROM GENE WHERE ID IN (SELECT ID FROM GENE WHERE GENE_NAME LIKE '%genic%') and ID != 11627;


