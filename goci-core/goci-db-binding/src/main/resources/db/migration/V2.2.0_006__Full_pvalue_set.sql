/*
################################################################################
Update study table with the field full_pvalue_set

author: Cinzia Malangone
date:    19/10/2016
version: 2.2.0.06
################################################################################
*/
--------------------------------------------------------
-- Add full_pvalue_set field to study table
--------------------------------------------------------

ALTER TABLE "STUDY" ADD ("FULL_PVALUE_SET" NUMBER(1,0));

--------------------------------------------------------
-- Populate full_pvalue_set field in study table
--------------------------------------------------------

UPDATE STUDY
SET FULL_PVALUE_SET = 0
WHERE FULL_PVALUE_SET is null;


