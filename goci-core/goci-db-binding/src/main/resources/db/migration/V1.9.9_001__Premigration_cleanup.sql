/*

################################################################################
Migration script to prepare NHGRI GWAS database dump for EBI migration.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Tony Burdett
date:    January 9th 2015
version: 1.9.9.001 (pre-2.0)
################################################################################

*/

--------------------------------------------------------
-- Essential cleanup on SNP data
--------------------------------------------------------
--  Clean chromosome position data in association table
UPDATE GWASSTUDIESSNP SET (CHR_ID, CHR_POS) = (SELECT CHR_ID, CHR_POS FROM GWASSTUDIESSNP WHERE ID = 16249) WHERE ID = 15489;
UPDATE GWASSTUDIESSNP SET (CHR_ID, CHR_POS) = (SELECT CHR_ID, CHR_POS FROM GWASSTUDIESSNP WHERE ID = 24628) WHERE ID = 15504;
UPDATE GWASSTUDIESSNP SET (CHR_ID, CHR_POS) = (SELECT CHR_ID, CHR_POS FROM GWASSTUDIESSNP WHERE ID = 15506) WHERE ID = 14622;
UPDATE GWASSTUDIESSNP SET (CHR_ID, CHR_POS) = (SELECT CHR_ID, CHR_POS FROM GWASSTUDIESSNP WHERE ID = 42708) WHERE ID = 15499;
