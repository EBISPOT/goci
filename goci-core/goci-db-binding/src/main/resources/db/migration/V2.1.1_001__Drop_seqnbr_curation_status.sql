/*
################################################################################
Migration script to update CURATION_STATUS with new columns required for
mapping pipeline.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    February 19th 2016
version: 2.1.1.001
################################################################################
*/
--------------------------------------------------------
--  DROP SEQNBR COLUMN
--------------------------------------------------------

ALTER TABLE CURATION_STATUS DROP COLUMN SEQNBR;
