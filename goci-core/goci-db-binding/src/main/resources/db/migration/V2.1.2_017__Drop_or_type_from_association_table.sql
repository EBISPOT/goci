/*
################################################################################
Migration script drop OR_TYPE from ASSOCIATION table.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    April 07th 2016
version: 2.1.2.017
################################################################################

#######################################
#  ALTER TABLE  #
#######################################
*/
ALTER TABLE "ASSOCIATION" DROP COLUMN OR_TYPE;