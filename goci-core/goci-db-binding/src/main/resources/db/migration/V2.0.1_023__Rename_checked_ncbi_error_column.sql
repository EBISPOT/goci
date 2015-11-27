/*

################################################################################
Migration script to rename CHECKEDNCBIERROR column in HOUSEKEEPING table.

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    July 29th 2015
version: 2.0.1.023
################################################################################
*/

--------------------------------------------------------
--  RENAME COLUMN
--------------------------------------------------------

ALTER TABLE HOUSEKEEPING RENAME COLUMN CHECKEDNCBIERROR TO CHECKED_MAPPING_ERROR;