/*
################################################################################
Migration script to populate TARGETED_ARRAY column in the STUDY table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:    May 13th 2016
version: 2.1.2.026
################################################################################
*/


--------------------------------------------------------
--  Update for Table STUDY
--------------------------------------------------------
    UPDATE "STUDY" SET TARGETED_ARRAY=0;