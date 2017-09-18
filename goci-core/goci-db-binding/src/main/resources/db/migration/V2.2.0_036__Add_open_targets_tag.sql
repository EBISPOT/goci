/*
################################################################################
Add OPEN_TARGETS column to STUDY table

This column is used to indicate a study being curated for the Open Targets project.

author:  Dani Welter
date:    15th September 2017
version: 2.2.0.36
################################################################################
*/
--------------------------------------------------------
-- Add OPEN_TARGETS field to study table
--------------------------------------------------------

ALTER TABLE "STUDY" ADD ("OPEN_TARGETS" NUMBER(1,0));

--------------------------------------------------------
-- Populate OPEN_TARGETS field in study table
--------------------------------------------------------

Update STUDY set OPEN_TARGETS = 0;
