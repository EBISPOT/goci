/*
################################################################################
Add column to Ensembl_RestCall_history table

Designed to improve Ensembl ReMapping.
The field ENSEMBL_NEXT_RELEASE is used during the migration between Ensembl release.


author: Cinzia Malangone
date:    19 th May 2017
version: 2.2.0.31
################################################################################
*/
--------------------------------------------------------
-- ALTER table ENSEMBL_RESTCALL_HISTORY
--------------------------------------------------------

ALTER TABLE "ENSEMBL_RESTCALL_HISTORY" ADD "ENSEMBL_SWAP_RELEASE" VARCHAR2(10 CHAR);

CREATE INDEX ENSEMBL_RESTCALL_HISTORY_idx5 ON ENSEMBL_RESTCALL_HISTORY (ENSEMBL_SWAP_RELEASE);

CREATE INDEX ENSEMBL_RESTCALL_HISTORY_idx6 ON ENSEMBL_RESTCALL_HISTORY (ENSEMBL_PARAM, ENSEMBL_SWAP_RELEASE );

CREATE INDEX ENSEMBL_RESTCALL_HISTORY_idx7 ON ENSEMBL_RESTCALL_HISTORY (REQUEST_TYPE, ENSEMBL_PARAM, ENSEMBL_SWAP_RELEASE);

CREATE INDEX ENSEMBL_RESTCALL_HISTORY_idx8 ON ENSEMBL_RESTCALL_HISTORY (REQUEST_TYPE, ENSEMBL_PARAM, ENSEMBL_VERSION, ENSEMBL_SWAP_RELEASE );