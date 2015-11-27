/*

################################################################################
Migration script to update ASSOCIATION attributes SNP_INTERACTION and
MULTI_SNP_HAPLOTYPE for 3 associations not captured by migrations scripts no. 48
and no. 49 as they do not have any linked loci.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    May 29th 2015
version: 1.9.9.050 (pre 2.0)
################################################################################

--------------------------------------------------------
--  UPDATE ASSOCIATIONS
--------------------------------------------------------
*/

UPDATE ASSOCIATION SET SNP_INTERACTION = 0, MULTI_SNP_HAPLOTYPE = 0 WHERE ID = 6118;
UPDATE ASSOCIATION SET SNP_INTERACTION = 0, MULTI_SNP_HAPLOTYPE = 0 WHERE ID = 6165;
UPDATE ASSOCIATION SET SNP_INTERACTION = 0, MULTI_SNP_HAPLOTYPE = 0 WHERE ID = 20173;