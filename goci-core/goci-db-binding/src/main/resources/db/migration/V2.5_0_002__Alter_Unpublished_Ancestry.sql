/*

################################################################################

Increase column sizes for unpublished tables

author: Trish Whetzel
date:    15 June 2020
version: 2.5.0.002

################################################################################
*/
ALTER TABLE UNPUBLISHED_ANCESTRY
    MODIFY (ANCESTRY_DESCRIPTION VARCHAR2(4000 BYTE) );

ALTER TABLE UNPUBLISHED_ANCESTRY
    MODIFY (SAMPLE_DESCRIPTION VARCHAR2(4000 BYTE) );