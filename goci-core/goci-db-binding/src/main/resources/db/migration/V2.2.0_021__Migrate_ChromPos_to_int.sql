/*

################################################################################
Migration script to change to column type of chromosome position from string to
number

author:  Dani Welter
date:    January 25th 2017
version: 2.2.0.021
################################################################################
*/

--------------------------------------------------------
-- Add temporary new column
--------------------------------------------------------

ALTER TABLE "LOCATION"
   ADD "CHROM_POS" NUMBER(19,0);

--------------------------------------------------------
-- Copy chromosome_position content to new column
--------------------------------------------------------

UPDATE "LOCATION"
    SET CHROM_POS = TO_NUMBER(CHROMOSOME_POSITION);

--------------------------------------------------------
-- Drop the old chromosome_position
--------------------------------------------------------

ALTER TABLE "LOCATION"
   DROP COLUMN "CHROMOSOME_POSITION";

--------------------------------------------------------
-- Add the new chromosome_position column
--------------------------------------------------------

ALTER TABLE "LOCATION"
   ADD "CHROMOSOME_POSITION" NUMBER(19,0);

--------------------------------------------------------
-- Copy chrom_pos content to new column
--------------------------------------------------------

UPDATE "LOCATION"
    SET CHROMOSOME_POSITION = CHROM_POS;

--------------------------------------------------------
-- Drop temporary column
--------------------------------------------------------

ALTER TABLE "LOCATION"
   DROP COLUMN "CHROM_POS";