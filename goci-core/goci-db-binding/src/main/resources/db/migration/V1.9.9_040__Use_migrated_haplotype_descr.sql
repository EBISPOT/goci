/*

################################################################################
Migration script to change the locus description to the migrated description
for haplotypes

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:    April 22nd 2015
version: 1.9.9.038 (pre 2.0)
################################################################################

--------------------------------------------------------
--  UPDATE DESCRIPTION TO MIGRATED DESCRIPTION FOR HAPLOTYPES
--------------------------------------------------------
*/


  UPDATE LOCUS SET DESCRIPTION = MIGRATED_DESCRIPTION WHERE MIGRATED_DESCRIPTION LIKE '%aplotype%';
