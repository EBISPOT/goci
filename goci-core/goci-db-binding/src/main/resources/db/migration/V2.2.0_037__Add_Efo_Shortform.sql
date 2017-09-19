/*
################################################################################
Add SHORT_FORM column to EFO_TRAIT table

This column is used to store the URI short form or ID of an ontology term.

author:  Dani Welter
date:    158h September 2017
version: 2.2.0.37
################################################################################
*/
--------------------------------------------------------
-- Add SHORT_FORM field to EFO_TRAIT table
--------------------------------------------------------

ALTER TABLE "EFO_TRAIT" ADD ("SHORT_FORM" VARCHAR2(255 CHAR));

