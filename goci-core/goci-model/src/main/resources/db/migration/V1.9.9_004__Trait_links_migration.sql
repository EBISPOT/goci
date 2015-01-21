/*

################################################################################
Migration script to adapt additional data on trait mappings from NHGRI GWAS
database dump into revised EBI schema (this info wasn't included in 002)

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Tony Burdett
date:    January 21st 2015
version: 1.9.9.004 (pre 2.0)
################################################################################

*/

/*
#############################################
#  POPULATE JOIN TABLES FROM EXISTING DATA  #
#############################################
*/

--------------------------------------------------------
--  Links for Table ASSOCIATION_EFO_TRAIT
--------------------------------------------------------

INSERT INTO ASSOCIATION_EFO_TRAIT (ASSOCIATION_ID, EFO_TRAIT_ID)
SELECT g.GWASSTUDIESSNPID, g.TRAITID FROM GWASEFOSNPXREF g
LEFT JOIN GWASSTUDIESSNP gs ON g.GWASSTUDIESSNPID = gs.ID
WHERE gs.ID IS NOT NULL;

--------------------------------------------------------
--  Links for Table STUDY_EFO_TRAIT
--------------------------------------------------------

INSERT INTO STUDY_EFO_TRAIT (STUDY_ID, EFO_TRAIT_ID)
SELECT STUDYID, TRAITID FROM GWASEFOXREF g
LEFT JOIN GWASSTUDIES gs ON g.STUDYID = gs.ID
WHERE gs.ID IS NOT NULL;

