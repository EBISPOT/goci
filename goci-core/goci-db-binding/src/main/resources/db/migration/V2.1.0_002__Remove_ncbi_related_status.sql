/*
################################################################################
Migration script to remove statuses 'Send to NCBI', 'NCBI pipeline error'
and 'Data import error'.

https://www.ebi.ac.uk/panda/jira/browse/GOCI-1188

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    Dec 11th 2015
version: 2.1.0.002
################################################################################
*/
--------------------------------------------------------
--  REMOVE ROWS
--------------------------------------------------------

DELETE FROM CURATION_STATUS WHERE STATUS IN ('Send to NCBI','NCBI pipeline error','Data import error');
