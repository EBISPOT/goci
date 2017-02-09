/*

################################################################################
Migration script to drop the STUDY_SNP table, a legacy table left over from
the initial migration

author:  Dani Welter
date:    January 25th 2017
version: 2.2.0.020
################################################################################
*/

--------------------------------------------------------
-- Remove STUDY_SNP
--------------------------------------------------------

DROP TABLE STUDY_SNP;