/*

################################################################################
Migration script to set SNP_CHECKED value in Association table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    February 09th 2015
version: 1.9.9.021 (pre 2.0)
################################################################################

*/


--------------------------------------------------------
--  Modify Table ASSOCIATION
--------------------------------------------------------
ALTER TABLE "ASSOCIATION" ADD ("SNP_CHECKED" NUMBER(1,0));

MERGE INTO ASSOCIATION a
    USING (SELECT '0' AS SNP_CHECKED, ASSOCIATION_ID FROM ASSOCIATION_REPORT WHERE SNP_PENDING = '1') ar
    ON (a.ID = ar.ASSOCIATION_ID)
    WHEN MATCHED THEN UPDATE SET a.SNP_CHECKED = ar.SNP_CHECKED;

MERGE INTO ASSOCIATION a
    USING (SELECT '1' AS SNP_CHECKED, ASSOCIATION_ID FROM ASSOCIATION_REPORT WHERE SNP_PENDING = '0') ar
    ON (a.ID = ar.ASSOCIATION_ID)
    WHEN MATCHED THEN UPDATE SET a.SNP_CHECKED = ar.SNP_CHECKED;

ALTER TABLE "ASSOCIATION_REPORT" DROP COLUMN "SNP_PENDING";