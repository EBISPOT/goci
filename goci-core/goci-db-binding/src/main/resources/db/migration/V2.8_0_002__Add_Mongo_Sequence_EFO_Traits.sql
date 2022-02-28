/*

################################################################################

Migration script to ALTER table EFO_TRAIT  TO add column MONGO_SEQ_ID

author: Sajo John
date:    21 Jan 2022
version: 2.8.0.001
################################################################################
*/

--------------------------------------------------------
--  Add MONGO_SEQ_ID to  EFO_TRAIT
--------------------------------------------------------
ALTER TABLE "EFO_TRAIT" ADD ("MONGO_SEQ_ID" VARCHAR2(24 CHAR));
