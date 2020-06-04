/*

################################################################################

Add additional columns to unpublished_studies

author: Jon Stewart
date:    06 April 2020
version: 2.4.0.016

################################################################################
*/
ALTER TABLE UNPUBLISHED_STUDY
ADD (ARRAY_MANUFACTURER VARCHAR2(50 BYTE) );
alter table "GWAS"."UNPUBLISHED_ANCESTRY" rename column "SIZE" to "SAMPLE_SIZE"