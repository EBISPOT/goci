/*
################################################################################
Migration script drop CHROMOSOME_NAME and CHROMOSOME_POSITION columns
from SINGLE_NUCLEOTIDE_POLYMORPHISM table.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    July 29st 2015
version: 2.0.1.021
################################################################################

#######################################
#  ALTER TABLE  #
#######################################
*/

ALTER TABLE "SINGLE_NUCLEOTIDE_POLYMORPHISM" DROP COLUMN CHROMOSOME_NAME;
ALTER TABLE "SINGLE_NUCLEOTIDE_POLYMORPHISM" DROP COLUMN CHROMOSOME_POSITION;