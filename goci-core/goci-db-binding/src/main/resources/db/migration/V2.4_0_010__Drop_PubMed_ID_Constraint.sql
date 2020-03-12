/*

################################################################################
Migration script to drop required constraint for publication pubmed ID column
author: Jon Stewart
date:    03 March 2020
version: 2.4.0.010
################################################################################
*/

alter table "GWAS"."PUBLICATION" drop constraint "SYS_C0089213"