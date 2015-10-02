/*

################################################################################
Drop PUBMED_ID_ERROR column from association report

Designed for execution with Flyway database migrations tool.

author:  Tony Burdett
date:    February 26th 2015
version: 1.9.9.031 (pre 2.0)
################################################################################

*/

--------------------------------------------------------
--  Mods for Table ASSOCIATION_REPORT
--------------------------------------------------------

ALTER TABLE ASSOCIATION_REPORT DROP COLUMN PUBMED_ID_ERROR;
