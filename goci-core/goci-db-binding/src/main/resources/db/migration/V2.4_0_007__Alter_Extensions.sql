/*

################################################################################
Migration script to add study_description column to study_extension
author: Jon Stewart
date:    05 Feb 2020
version: 2.4.0.007
################################################################################
*/

alter table "GWAS"."STUDY_EXTENSION" ADD STUDY_DESCRIPTION VARCHAR(255);

alter table "GWAS"."ANCESTRY_EXTENSION" DROP (ANCESTRY_DESCRIPTOR);
