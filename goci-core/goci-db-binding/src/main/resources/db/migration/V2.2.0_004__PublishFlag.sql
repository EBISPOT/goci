/*
################################################################################
Update housekeeping table with column for publish flag and populate field

author: Dani Welter
date:    Sep 21st 2016
version: 2.2.0.04
################################################################################
*/
--------------------------------------------------------
-- Add publish flag field to housekeeping table
--------------------------------------------------------

ALTER TABLE "HOUSEKEEPING" ADD ("IS_PUBLISHED" NUMBER(1,0));


--------------------------------------------------------
-- Populate publish flag field in housekeeping table
--------------------------------------------------------

UPDATE HOUSEKEEPING
SET IS_PUBLISHED = 1
WHERE CATALOG_PUBLISH_DATE IS NOT NULL
AND CATALOG_UNPUBLISH_DATE IS NULL;


UPDATE HOUSEKEEPING
SET IS_PUBLISHED = 0
WHERE IS_PUBLISHED is null;


