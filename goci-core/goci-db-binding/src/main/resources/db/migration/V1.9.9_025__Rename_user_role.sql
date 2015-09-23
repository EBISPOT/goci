/*

################################################################################
Migration script to rename security table

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    February 13th 2015
version: 1.9.9.025 (pre 2.0)
################################################################################

*/


--------------------------------------------------------
--  Modify Table ROLE
--------------------------------------------------------
ALTER TABLE "ROLE" RENAME TO "SECURE_ROLE";

--------------------------------------------------------
--  Modify Table USER
--------------------------------------------------------
ALTER TABLE "USER" RENAME TO "SECURE_USER";
