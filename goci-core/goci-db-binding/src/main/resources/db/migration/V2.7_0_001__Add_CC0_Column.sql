/*

################################################################################

Migration script to add AGREED_TO_CC0 to STUDY and UNPUBLISHED_STUDY tables

author: Ala Abid
date:    16 Mar 2021
version: 2.7.0.001
################################################################################
*/

--------------------------------------------------------
--  CREATE TABLE, INDEX, FOREIGN KEY
--------------------------------------------------------

ALTER TABLE "STUDY" ADD "AGREED_TO_CC0" NUMBER(1,0);
ALTER TABLE "UNPUBLISHED_STUDY" ADD "AGREED_TO_CC0" NUMBER(1,0);
