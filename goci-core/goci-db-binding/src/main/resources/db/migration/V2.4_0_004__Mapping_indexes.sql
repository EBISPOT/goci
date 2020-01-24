/*

################################################################################
Migration script to add indexes to improve mapping pipeline queries
author: Jon Stewart
date:    24 Jan 2020
version: 2.4.0.004
################################################################################
*/

CREATE INDEX "GWAS"."LOCUS_RA_ID_IDX" ON "GWAS"."LOCUS_RISK_ALLELE" ("RISK_ALLELE_ID");
CREATE INDEX "GWAS"."GENE_NAME_IDX" ON "GWAS"."GENE" ("GENE_NAME");
CREATE INDEX "GWAS"."LOC_POS_IDX" ON "GWAS"."LOCATION" ("CHROMOSOME_POSITION");
CREATE INDEX "GWAS"."SNP_LOC_ID_IDX" ON "GWAS"."SNP_LOCATION" ("LOCATION_ID");
CREATE INDEX "GWAS"."LOC_REG_ID_IDX" ON "GWAS"."LOCATION" ("REGION_ID");
CREATE INDEX "GWAS"."GC_LOC_ID_IDX" ON "GWAS"."GENOMIC_CONTEXT" ("LOCATION_ID");