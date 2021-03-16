/*
################################################################################
Migration script to change TRAIT column in DISEASE_TRAIT TABLE TO UNIQUE in order to avoid duplicated entries

author: Abayomi Mosaku
date:    28 November 2020
version: 2.6.0.006
################################################################################
*/

ALTER TABLE DISEASE_TRAIT
    ADD CONSTRAINT TRAIT_COLUMN_UNIQUE UNIQUE (TRAIT);
COMMIT;
