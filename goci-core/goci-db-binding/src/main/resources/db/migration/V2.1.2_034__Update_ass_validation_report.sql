/*
################################################################################
Update ASSOCIATION_VALIDATION_REPORT table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).


author: Emma Hastings
date:    Jun 24th 2016
version: 2.1.2.034
################################################################################
*/
--------------------------------------------------------
-- ALTER table ASSOCIATION_VALIDATION_REPORT
--------------------------------------------------------
ALTER TABLE "ASSOCIATION_VALIDATION_REPORT" ADD "ERROR_CHECKED_BY_CURATOR" NUMBER(1,0);