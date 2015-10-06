/*
################################################################################
Migration script to from ASSOCIATION table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    Oct 06th 2015
version: 1.9.9.060 (pre 2.0)
################################################################################
*/
--------------------------------------------------------
--  Drop columns
--------------------------------------------------------

ALTER TABLE ASSOCIATION DROP COLUMN ALLELE;
ALTER TABLE ASSOCIATION DROP COLUMN AUTHOR_REPORTED_GENE;
ALTER TABLE ASSOCIATION DROP COLUMN PVALUE_FLOAT;
ALTER TABLE ASSOCIATION DROP COLUMN STRONGEST_ALLELE;