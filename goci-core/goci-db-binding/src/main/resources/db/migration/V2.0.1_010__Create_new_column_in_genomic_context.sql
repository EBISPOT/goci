/*

################################################################################
Migration script to create:

- IS_CLOSEST_GENE column in GENOMIC_CONTEXT

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    July 17th 2015
version: 2.0.1.010
################################################################################

#######################################
#  ALTER GENOMIC_CONTEXT
#######################################
*/
ALTER TABLE "GENOMIC_CONTEXT" ADD "IS_CLOSEST_GENE" VARCHAR2(255 CHAR);
