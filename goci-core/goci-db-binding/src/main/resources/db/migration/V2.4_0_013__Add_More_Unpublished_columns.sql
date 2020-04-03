/*

################################################################################

Add additional columns to unpublished_studies

author: Jon Stewart
date:    03 April 2020
version: 2.4.0.013

################################################################################
*/
ALTER TABLE UNPUBLISHED_STUDY
ADD (CREATED_DATE DATE );
ALTER TABLE UNPUBLISHED_STUDY
ADD (GENOTYPING_TECHNOLOGY VARCHAR(255) );
ALTER TABLE UNPUBLISHED_STUDY
ADD (STATISTICAL_MODEL VARCHAR(255) );
ALTER TABLE BODY_OF_WORK
ADD (DOI VARCHAR(255) );
