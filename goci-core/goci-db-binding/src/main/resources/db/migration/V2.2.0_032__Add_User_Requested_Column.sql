/*
################################################################################
Add USER_REQUESTED column to STUDY table

This column is used to indicate a user requested study.

author:  Xin He
date:    21st June 2017
version: 2.2.0.32
################################################################################
*/
--------------------------------------------------------
-- Add USER_REQUESTED field to study table
--------------------------------------------------------

ALTER TABLE "STUDY" ADD ("USER_REQUESTED" NUMBER(1));

--------------------------------------------------------
-- Populate USER_REQUESTED field in study table
--------------------------------------------------------

Update STUDY set USER_REQUESTED = 0;
