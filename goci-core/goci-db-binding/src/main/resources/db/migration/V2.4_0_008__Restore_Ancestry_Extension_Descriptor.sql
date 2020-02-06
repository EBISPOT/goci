/*

################################################################################
Migration script to restore ancestry_descriptor column to ancestry_extension
author: Jon Stewart
date:    06 Feb 2020
version: 2.4.0.008
################################################################################
*/

alter table "GWAS"."ANCESTRY_EXTENSION" ADD ANCESTRY_DESCRIPTOR VARCHAR(255);
