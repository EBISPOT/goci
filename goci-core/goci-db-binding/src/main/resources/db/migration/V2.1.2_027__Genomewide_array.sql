/*
################################################################################
Migration script to add and populate GENOMEWIDE_ARRAY column to the STUDY table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:    May 18th 2016
version: 2.1.2.027
################################################################################
*/


--------------------------------------------------------
--  Update for Table STUDY
--------------------------------------------------------
    ALTER TABLE "STUDY" ADD "GENOMEWIDE_ARRAY" NUMBER(1,0);



--------------------------------------------------------
--  Populate new column in table STUDY
--------------------------------------------------------
    UPDATE "STUDY" SET GENOMEWIDE_ARRAY=1 WHERE TARGETED_ARRAY=0;

    UPDATE "STUDY" SET GENOMEWIDE_ARRAY=0 WHERE TARGETED_ARRAY=1;