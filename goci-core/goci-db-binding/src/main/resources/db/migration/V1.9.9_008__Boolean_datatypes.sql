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
ALTER TABLE "STUDY" ADD ("CNV_BOOL" NUMBER(1,0));
ALTER TABLE "STUDY" ADD ("GXE_BOOL" NUMBER(1,0));
ALTER TABLE "STUDY" ADD ("GXG_BOOL" NUMBER(1,0));

UPDATE STUDY a SET (CNV_BOOL, GXE_BOOL, GXG_BOOL) = (
    SELECT CNV, GXE, GXG FROM STUDY b WHERE b.ID = a.ID);

ALTER TABLE "STUDY" DROP COLUMN "CNV";
ALTER TABLE "STUDY" DROP COLUMN "GXE";
ALTER TABLE "STUDY" DROP COLUMN "GXG";

ALTER TABLE "STUDY" RENAME COLUMN "CNV_BOOL" TO "CNV";
ALTER TABLE "STUDY" RENAME COLUMN "GXE_BOOL" TO "GXE";
ALTER TABLE "STUDY" RENAME COLUMN "GXG_BOOL" TO "GXG";

--------------------------------------------------------
--  Modify Table ASSOCIATION
--------------------------------------------------------
ALTER TABLE "ASSOCIATION" ADD ("OR_TYPE_BOOL" NUMBER(1,0));
ALTER TABLE "ASSOCIATION" ADD ("SNP_INTERACTION_BOOL" NUMBER(1,0));
ALTER TABLE "ASSOCIATION" ADD ("MULTI_SNP_HAPLOTYPE_BOOL" NUMBER(1,0));

UPDATE ASSOCIATION a SET (OR_TYPE_BOOL, SNP_INTERACTION_BOOL, MULTI_SNP_HAPLOTYPE_BOOL) = (
    SELECT OR_TYPE, SNP_INTERACTION, MULTI_SNP_HAPLOTYPE FROM ASSOCIATION b WHERE b.ID = a.ID);

ALTER TABLE "ASSOCIATION" DROP COLUMN "OR_TYPE";
ALTER TABLE "ASSOCIATION" DROP COLUMN "SNP_INTERACTION";
ALTER TABLE "ASSOCIATION" DROP COLUMN "MULTI_SNP_HAPLOTYPE";

ALTER TABLE "ASSOCIATION" RENAME COLUMN "OR_TYPE_BOOL" TO "OR_TYPE";
ALTER TABLE "ASSOCIATION" RENAME COLUMN "SNP_INTERACTION_BOOL" TO "SNP_INTERACTION";
ALTER TABLE "ASSOCIATION" RENAME COLUMN "MULTI_SNP_HAPLOTYPE_BOOL" TO "MULTI_SNP_HAPLOTYPE";

--------------------------------------------------------
--  Modify Table HOUSEKEEPING
--------------------------------------------------------
ALTER TABLE "HOUSEKEEPING" ADD ("STUDY_SNP_LEVEL_ONE_BOOL" NUMBER(1,0));
ALTER TABLE "HOUSEKEEPING" ADD ("STUDY_SNP_LEVEL_TWO_BOOL" NUMBER(1,0));
ALTER TABLE "HOUSEKEEPING" ADD ("ETHNICITY_LEVEL_ONE_BOOL" NUMBER(1,0));
ALTER TABLE "HOUSEKEEPING" ADD ("ETHNICITY_LEVEL_TWO_BOOL" NUMBER(1,0));
ALTER TABLE "HOUSEKEEPING" ADD ("ETHNICITY_BF_BOOL" NUMBER(1,0));
ALTER TABLE "HOUSEKEEPING" ADD ("CHECKEDNCBIERROR_BOOL" NUMBER(1,0));

UPDATE HOUSEKEEPING a SET (STUDY_SNP_LEVEL_ONE_BOOL, STUDY_SNP_LEVEL_TWO_BOOL, ETHNICITY_LEVEL_ONE_BOOL, ETHNICITY_LEVEL_TWO_BOOL, ETHNICITY_BF_BOOL, CHECKEDNCBIERROR_BOOL) = (
    SELECT STUDY_SNP_CHECKED_LEVEL_ONE, STUDY_SNP_CHECKED_LEVEL_TWO, ETHNICITY_CHECKED_LEVEL_ONE, ETHNICITY_CHECKED_LEVEL_TWO, ETHNICITY_BACK_FILLED, CHECKEDNCBIERROR FROM HOUSEKEEPING b WHERE b.ID = a.ID);

ALTER TABLE "HOUSEKEEPING" DROP COLUMN "STUDY_SNP_CHECKED_LEVEL_ONE";
ALTER TABLE "HOUSEKEEPING" DROP COLUMN "STUDY_SNP_CHECKED_LEVEL_TWO";
ALTER TABLE "HOUSEKEEPING" DROP COLUMN "ETHNICITY_CHECKED_LEVEL_ONE";
ALTER TABLE "HOUSEKEEPING" DROP COLUMN "ETHNICITY_CHECKED_LEVEL_TWO";
ALTER TABLE "HOUSEKEEPING" DROP COLUMN "ETHNICITY_BACK_FILLED";
ALTER TABLE "HOUSEKEEPING" DROP COLUMN "CHECKEDNCBIERROR";

ALTER TABLE "HOUSEKEEPING" RENAME COLUMN "STUDY_SNP_LEVEL_ONE_BOOL" TO "STUDY_SNP_CHECKED_LEVEL_ONE";
ALTER TABLE "HOUSEKEEPING" RENAME COLUMN "STUDY_SNP_LEVEL_TWO_BOOL" TO "STUDY_SNP_CHECKED_LEVEL_TWO";
ALTER TABLE "HOUSEKEEPING" RENAME COLUMN "ETHNICITY_LEVEL_ONE_BOOL" TO "ETHNICITY_CHECKED_LEVEL_ONE";
ALTER TABLE "HOUSEKEEPING" RENAME COLUMN "ETHNICITY_LEVEL_TWO_BOOL" TO "ETHNICITY_CHECKED_LEVEL_TWO";
ALTER TABLE "HOUSEKEEPING" RENAME COLUMN "ETHNICITY_BF_BOOL" TO "ETHNICITY_BACK_FILLED";
ALTER TABLE "HOUSEKEEPING" RENAME COLUMN "CHECKEDNCBIERROR_BOOL" TO "CHECKEDNCBIERROR";