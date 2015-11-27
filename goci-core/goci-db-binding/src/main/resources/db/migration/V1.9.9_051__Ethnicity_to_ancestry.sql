/*

################################################################################
Migration script to update CURATION_STATUS values for ancestry-related
statuses from "ethnicity" to "ancestry"

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:    June 2nd 2015
version: 1.9.9.051 (pre 2.0)
################################################################################

--------------------------------------------------------
--  UPDATE CURATION_STATUS
--------------------------------------------------------
*/

UPDATE CURATION_STATUS SET STATUS = 'Level 1 ancestry done' WHERE ID = 1;
UPDATE CURATION_STATUS SET STATUS = 'Level 2 ancestry done' WHERE ID = 2;
