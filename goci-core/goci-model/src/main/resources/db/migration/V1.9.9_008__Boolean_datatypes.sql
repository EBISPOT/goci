/*

################################################################################
Migration script to set common boolean datatypes to booleans *correctly* in
the database

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Tony Burdett
date:    January 28st 2015
version: 1.9.9.008 (pre 2.0)
################################################################################

*/


--------------------------------------------------------
--  Modify Table STUDY
--------------------------------------------------------
ALTER TABLE "STUDY" MODIFY ("CNV" NUMBER(1,0));
ALTER TABLE "STUDY" MODIFY ("GXE" NUMBER(1,0));
ALTER TABLE "STUDY" MODIFY ("GXG" NUMBER(1,0));

--------------------------------------------------------
--  Modify Table ASSOCIATION
--------------------------------------------------------
ALTER TABLE "ASSOCIATION" MODIFY ("OR_TYPE" NUMBER(1,0));
ALTER TABLE "ASSOCIATION" MODIFY ("SNP_INTERACTION" NUMBER(1,0));
ALTER TABLE "ASSOCIATION" MODIFY ("MULTI_SNP_HAPLOTYPE" NUMBER(1,0));

--------------------------------------------------------
--  Modify Table HOUSEKEEPING
--------------------------------------------------------
ALTER TABLE "HOUSEKEEPING" MODIFY ("STUDY_SNP_CHECKED_LEVEL_ONE" NUMBER(1,0));
ALTER TABLE "HOUSEKEEPING" MODIFY ("STUDY_SNP_CHECKED_LEVEL_TWO" NUMBER(1,0));
ALTER TABLE "HOUSEKEEPING" MODIFY ("ETHNICITY_CHECKED_LEVEL_ONE" NUMBER(1,0));
ALTER TABLE "HOUSEKEEPING" MODIFY ("ETHNICITY_CHECKED_LEVEL_TWO" NUMBER(1,0));
ALTER TABLE "HOUSEKEEPING" MODIFY ("ETHNICITY_BACK_FILLED" NUMBER(1,0));
ALTER TABLE "HOUSEKEEPING" MODIFY ("CHECKEDNCBIERROR" NUMBER(1,0));

