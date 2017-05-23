/*

################################################################################
Migration script to accommodate GENE_ENTREZ_GENE and GENE_ENSEMBL_GENE table,
designed to model fact that genes can have multiple external database IDs.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    July 21st 2015
version: 2.0.1.015
################################################################################

#######################################
#  CREATE NEW TABLES AND CONSTRAINTS  #
#######################################
*/

--------------------------------------------------------
--  DDL for Table GENE_ENTREZ_GENE
--------------------------------------------------------
CREATE TABLE "GENE_ENTREZ_GENE" (
    "GENE_ID" NUMBER(19,0),
    "ENTREZ_GENE_ID" NUMBER(19,0));

--------------------------------------------------------
--  Constraints for Table GENE_ENTREZ_GENE
--------------------------------------------------------
ALTER TABLE "GENE_ENTREZ_GENE" MODIFY ("GENE_ID" NOT NULL ENABLE);
ALTER TABLE "GENE_ENTREZ_GENE" MODIFY ("ENTREZ_GENE_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table GENE_ENTREZ_GENE
--------------------------------------------------------
ALTER TABLE "GENE_ENTREZ_GENE" ADD CONSTRAINT "GENE_ID_TO_ENTREZ_FK" FOREIGN KEY ("GENE_ID")
REFERENCES "GENE" ("ID") ENABLE;
ALTER TABLE "GENE_ENTREZ_GENE" ADD CONSTRAINT "ENTREZ_GENE_ID_FK" FOREIGN KEY ("ENTREZ_GENE_ID")
REFERENCES "ENTREZ_GENE" ("ID") ENABLE;


--------------------------------------------------------
--  DDL for Table GENE_ENSEMBL_GENE
--------------------------------------------------------
CREATE TABLE "GENE_ENSEMBL_GENE" (
    "GENE_ID" NUMBER(19,0),
    "ENSEMBL_GENE_ID" NUMBER(19,0));

--------------------------------------------------------
--  Constraints for Table GENE_ENSEMBL_GENE
--------------------------------------------------------
ALTER TABLE "GENE_ENSEMBL_GENE" MODIFY ("GENE_ID" NOT NULL ENABLE);
ALTER TABLE "GENE_ENSEMBL_GENE" MODIFY ("ENSEMBL_GENE_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  Ref Constraints for Table GENE_ENSEMBL_GENE
--------------------------------------------------------
ALTER TABLE "GENE_ENSEMBL_GENE" ADD CONSTRAINT "GENE_ID_TO_ENSEMBL_FK" FOREIGN KEY ("GENE_ID")
REFERENCES "GENE" ("ID") ENABLE;
ALTER TABLE "GENE_ENSEMBL_GENE" ADD CONSTRAINT "ENSEMBL_GENE_ID_FK" FOREIGN KEY ("ENSEMBL_GENE_ID")
REFERENCES "ENSEMBL_GENE" ("ID") ENABLE;