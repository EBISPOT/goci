/*
################################################################################
Migration script to create:

- LAST_MAPPING_DATE column in ASSOCIATION
- LAST_MAPPING_PERFORMED_BY in ASSOCIATION

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    August 7th 2015
version: 2.0.1.029
################################################################################

#######################################
#  ALTER ASSOCIATION
#######################################
*/

ALTER TABLE "ASSOCIATION" ADD "LAST_MAPPING_DATE" DATE;
ALTER TABLE "ASSOCIATION" ADD "LAST_MAPPING_PERFORMED_BY" VARCHAR2(255 CHAR);
