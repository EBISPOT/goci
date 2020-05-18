/*

################################################################################

Disallow null values for snp-rs_id and gene-gene_name

author: Jon Stewart
date:    18 May 2020
version: 2.4.0.020

################################################################################
*/
ALTER TABLE GENE
MODIFY (GENE_NAME NOT NULL);

ALTER TABLE SINGLE_NUCLEOTIDE_POLYMORPHISM
MODIFY (RS_ID NOT NULL);
