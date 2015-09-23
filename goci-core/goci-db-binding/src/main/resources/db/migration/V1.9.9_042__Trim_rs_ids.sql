/*

################################################################################
Migration script to trim all RS_IDs of leading and trailing white spaces

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:    May 21st 2015
version: 1.9.9.042 (pre 2.0)
################################################################################

--------------------------------------------------------
--  TRIM RS_IDs IN SINGLE_NUCLEOTIDE_POLYMORPHISM
--------------------------------------------------------
*/


  UPDATE SINGLE_NUCLEOTIDE_POLYMORPHISM SET RS_ID = TRIM(RS_ID);

