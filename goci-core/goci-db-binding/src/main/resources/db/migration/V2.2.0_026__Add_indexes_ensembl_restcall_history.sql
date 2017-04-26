/*

################################################################################
Migration script to add indexes to Ensembl RestCall History

author:  Cinzia Malangone
date:    25 April 2017
version: 2.2.0.026
################################################################################
*/

CREATE INDEX ENSEMBL_RESTCALL_HISTORY_idx1 ON ENSEMBL_RESTCALL_HISTORY (ENSEMBL_VERSION);

CREATE INDEX ENSEMBL_RESTCALL_HISTORY_idx2 ON ENSEMBL_RESTCALL_HISTORY (ENSEMBL_PARAM);

CREATE INDEX ENSEMBL_RESTCALL_HISTORY_idx3 ON ENSEMBL_RESTCALL_HISTORY (ENSEMBL_PARAM, ENSEMBL_VERSION);

CREATE INDEX ENSEMBL_RESTCALL_HISTORY_idx4 ON ENSEMBL_RESTCALL_HISTORY (REQUEST_TYPE, ENSEMBL_PARAM, ENSEMBL_VERSION);
