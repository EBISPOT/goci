/*

################################################################################

Migration script to create the table EnsambleRestCallHistory
This table stores the Ensembl requests and the relative responses


author: Cinzia Malangone
date:    30th Jan. 2017
version: 2.2.0.022
################################################################################
*/

--------------------------------------------------------
--  CREATE TABLE, INDEX, FOREIGN KEY
--------------------------------------------------------
  CREATE TABLE "ENSEMBL_RESTCALL_HISTORY" (
     "ID" NUMBER(19,0),
     "REQUEST_TYPE" VARCHAR2(200 CHAR),
     "ENSEMBL_PARAM" VARCHAR2(250 CHAR),
     "ENSEMBL_URL"  VARCHAR2(255 CHAR),
     "ENSEMBL_RESPONSE"  CLOB,
     "ENSEMBL_ERROR" VARCHAR2(255 CHAR),
     "ENSEMBL_VERSION" VARCHAR2(10 CHAR),
     "LAST_UPDATE_TIME" TIMESTAMP
     );

  ALTER TABLE "ENSEMBL_RESTCALL_HISTORY" ADD PRIMARY KEY ("ID") ENABLE;

