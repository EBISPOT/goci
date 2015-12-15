/*

################################################################################
Migration script to rename drop SNP_REGION table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    July 29th 2015
version: 2.0.1.024
################################################################################
*/

--------------------------------------------------------
-- Remove SNP_REGION
--------------------------------------------------------

DROP TABLE SNP_REGION;