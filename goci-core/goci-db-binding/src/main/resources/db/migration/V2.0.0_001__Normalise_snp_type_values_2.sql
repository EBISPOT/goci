/*

################################################################################
Migration script to update SNP_TYPE values

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    April 15th 2015
version: 2.0.0.001
################################################################################

--------------------------------------------------------
--  Update SNP_TYPE of associations
--------------------------------------------------------
*/

UPDATE ASSOCIATION SET SNP_TYPE = lower(SNP_TYPE)
