/*

################################################################################
Migration script to drop ASSOCIATION link tables following Java migration
cleanup (see classes in db.migration package for more)

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Tony Burdett
date:    January 30th 2015
version: 1.9.9.013 (pre 2.0)
################################################################################

*/

--------------------------------------------------------
--  Drop now-unused tables
--------------------------------------------------------

DROP TABLE ASSOCIATION_REPORTED_GENE;

DROP TABLE ASSOCIATION_SNP;