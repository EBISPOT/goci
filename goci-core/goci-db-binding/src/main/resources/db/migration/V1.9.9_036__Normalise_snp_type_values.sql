/*

################################################################################
Migration script to update SNP_TYPE values

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    April 15th 2015
version: 1.9.9.036 (pre 2.0)
################################################################################

--------------------------------------------------------
--  Update SNP_TYPE of associations
--------------------------------------------------------
*/

-- KNOWN
UPDATE ASSOCIATION SET SNP_TYPE = 'known' WHERE SNP_TYPE = 'know';
UPDATE ASSOCIATION SET SNP_TYPE = 'known' WHERE SNP_TYPE = 'Known';
-- NOVEL
UPDATE ASSOCIATION SET SNP_TYPE = 'novel' WHERE SNP_TYPE like 'Novel%';