/*

################################################################################

Migration script to create ANCESTRY_EXTENSION table

author: Jon Stewart
date:    12 Dec 2019
version: 2.3.0.008
################################################################################
*/
--------------------------------------------------------
--  DDL for Table ANCESTRY_EXTENSION
--------------------------------------------------------

  CREATE TABLE "GWAS"."ANCESTRY_EXTENSION"
   (	"ID" NUMBER(19,0),
	"ANCESTRY_ID" NUMBER(19,0),
	"NUMBER_CASES" NUMBER(10,0),
	"NUMBER_CONTROLS" NUMBER(10,0),
	"SAMPLE_DESCRIPTION" VARCHAR2(255 BYTE),
	"ANCESTRY_DESCRIPTOR" VARCHAR2(255 BYTE),
	"ISOLATED_POPULATION" VARCHAR2(255 BYTE)
   ) SEGMENT CREATION DEFERRED
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index ANCESTRY_EXTENSION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "GWAS"."ANCESTRY_EXTENSION_PK" ON "GWAS"."ANCESTRY_EXTENSION" ("ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  Constraints for Table ANCESTRY_EXTENSION
--------------------------------------------------------

  ALTER TABLE "GWAS"."ANCESTRY_EXTENSION" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "GWAS"."ANCESTRY_EXTENSION" ADD CONSTRAINT "ANCESTRY_EXTENSION_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "USERS"  ENABLE;


