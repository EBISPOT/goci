/*

################################################################################
Migration script to add unique key to publication pubmed ID column
author: Jon Stewart
date:    02 March 2020
version: 2.4.0.009
################################################################################
*/
alter table "GWAS"."PUBLICATION" add constraint PUBMED_ID_UNIQUE unique("PUBMED_ID")