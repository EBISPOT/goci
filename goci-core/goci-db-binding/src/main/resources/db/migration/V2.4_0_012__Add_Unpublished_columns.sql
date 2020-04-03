/*

################################################################################

Add additional columns to unpublished_studies

author: Jon Stewart
date:    02 April 2020
version: 2.4.0.012

################################################################################
*/
ALTER TABLE UNPUBLISHED_STUDY
MODIFY (CHECKSUM VARCHAR2(50 BYTE) );
ALTER TABLE UNPUBLISHED_STUDY
ADD (GLOBUS_FOLDER VARCHAR2(255) );
ALTER TABLE UNPUBLISHED_STUDY
ADD (SUBMISSION_ID VARCHAR2(255) );
