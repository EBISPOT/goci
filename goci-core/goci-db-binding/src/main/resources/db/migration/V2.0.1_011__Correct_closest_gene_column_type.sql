/*

################################################################################
Migration script to create update type of column IS_CLOSEST_GENE in GENOMIC_CONTEXT

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    July 17th 2015
version: 2.0.1.011
################################################################################

#######################################
#  ALTER GENOMIC_CONTEXT
#######################################
*/

ALTER TABLE "GENOMIC_CONTEXT" MODIFY "IS_CLOSEST_GENE" NUMBER(1,0);
