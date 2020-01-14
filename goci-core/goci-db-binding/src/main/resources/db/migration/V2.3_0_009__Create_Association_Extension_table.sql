/*

################################################################################

Migration script to create ASSOCIATION_EXTENSION table

author: Jon Stewart
date:    12 Dec 2019
version: 2.3.0.008
################################################################################
*/
--------------------------------------------------------
--  DDL for Table ASSOCIATION_EXTENSION
--------------------------------------------------------

  CREATE TABLE "GWAS"."ASSOCIATION_EXTENSION"
   (	"ID" NUMBER(19,0) NOT NULL ENABLE,
	"ASSOCIATION_ID" NUMBER(19,0),
	"EFFECT_ALLELE" VARCHAR2(255 BYTE),
	"OTHER_ALLELE" VARCHAR2(255 BYTE),
	 CONSTRAINT "ASSOCIATION_EXTENSION_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "USERS"  ENABLE
   ) SEGMENT CREATION DEFERRED
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
