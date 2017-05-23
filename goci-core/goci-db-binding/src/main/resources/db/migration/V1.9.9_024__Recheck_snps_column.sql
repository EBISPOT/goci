/*

################################################################################
Migration script to set SNPS_RECHECKED value in Housekeeping table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Tony Burdett
date:    February 12th 2015
version: 1.9.9.024 (pre 2.0)
################################################################################

*/


--------------------------------------------------------
--  Modify Table HOUSEKEEPING
--------------------------------------------------------
ALTER TABLE "HOUSEKEEPING" ADD ("SNPS_RECHECKED" NUMBER(1,0));

MERGE INTO HOUSEKEEPING h
    USING (SELECT RECHECKSNPS, ID FROM GWASSTUDIES) gs
    ON (h.ID = gs.ID)
    WHEN MATCHED THEN UPDATE SET h.SNPS_RECHECKED = gs.RECHECKSNPS;
