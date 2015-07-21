/*
################################################################################
Migration script drop ENTREZ_GENE_ID and ENSEMBL_GENE_ID columns from GENE table.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    July 21st 2015
version: 1.9.9.072 (pre 2.0)
################################################################################

#######################################
#  ALTER TABLE  #
#######################################
*/

ALTER TABLE "GENE" DROP COLUMN ENTREZ_GENE_ID;
ALTER TABLE "GENE" DROP COLUMN ENSEMBL_GENE_ID;