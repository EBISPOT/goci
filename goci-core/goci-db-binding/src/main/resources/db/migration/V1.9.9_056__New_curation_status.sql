/*

################################################################################
Migration script to create a new curation status - Pending author query

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Emma Hastings
date:    Oct 02nd 2015
version: 1.9.9.056 (pre 2.0)
################################################################################
*/

--------------------------------------------------------
-- INSERT INTO CURATION_STATUS
--------------------------------------------------------

INSERT INTO CURATION_STATUS(ID, SEQNBR, STATUS) VALUES(66, 16, 'Pending author query');
