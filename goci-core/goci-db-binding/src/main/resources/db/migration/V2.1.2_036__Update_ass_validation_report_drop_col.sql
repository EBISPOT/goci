/*
################################################################################
Update ASSOCIATION_VALIDATION_REPORT table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).


author: Emma Hastings
date:    Jul 15th 2016
version: 2.1.2.036
################################################################################
*/
--------------------------------------------------------
-- ALTER table ASSOCIATION_VALIDATION_REPORT
--------------------------------------------------------
ALTER TABLE "ASSOCIATION_VALIDATION_REPORT" DROP COLUMN "ERROR_CHECKED_BY_CURATOR";