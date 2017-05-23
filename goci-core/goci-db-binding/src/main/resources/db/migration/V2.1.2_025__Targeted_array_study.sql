/*
################################################################################
Migration script to add TARGETED_ARRAY column to the STUDY table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:    May 13th 2016
version: 2.1.2.025
################################################################################
*/


--------------------------------------------------------
--  Update for Table STUDY
--------------------------------------------------------
    ALTER TABLE "STUDY" ADD "TARGETED_ARRAY" NUMBER(1,0);