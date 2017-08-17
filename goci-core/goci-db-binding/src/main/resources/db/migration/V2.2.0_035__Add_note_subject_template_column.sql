/*
################################################################################
Add TEMPLATE column to note Subject table

This column is used to store template for note with a specific subject

author:  Xin He
date:    12nd July 2017
version: 2.2.0.35
################################################################################
*/
--------------------------------------------------------
-- Add TEMPLATE field to note Subject table
--------------------------------------------------------

ALTER TABLE NOTE_SUBJECT ADD (TEMPLATE VARCHAR2(255 CHAR) );

--------------------------------------------------------
-- Populate pre-defined template
--------------------------------------------------------

Update NOTE_SUBJECT set TEMPLATE ='**Study files
**Study design
**Platform/ SNP n/ imputation
**Sample n/ ancestry/ CoR
            Discovery:
            Replication:
**SNPs
**Summary stats
**EFO/ trait
**Queries'
where SUBJECT = 'Initial extraction' ;


Update NOTE_SUBJECT set TEMPLATE ='**Study files
**Study design
**Platform/ SNP n/ imputation
**Sample n/ ancestry/ CoR
            Discovery:
            Replication:
**SNPs
**Summary stats
**EFO/ trait
**Queries'
where SUBJECT = 'Review/secondary extraction' ;


