/*

################################################################################

Migration script to create PUBLICATION_EXTENSION table

author: Jon Stewart
date:    12 Dec 2019
version: 2.3.0.006
################################################################################
*/
--------------------------------------------------------
--  DDL for Table PUBLICATION_EXTENSION
--------------------------------------------------------

  CREATE TABLE "GWAS"."PUBLICATION_EXTENSION"
   (		"ID" NUMBER(*,0),
    "PUBLICATION_ID" NUMBER(19,0),
	"CORRESPONDING_AUTHOR_NAME" VARCHAR2(255 BYTE),
	"CORRESPONDING_AUTHOR_EMAIL" VARCHAR2(255 BYTE),
	"CORRESPONDING_AUTHOR_ORC_ID" VARCHAR2(255 BYTE)
   ) SEGMENT CREATION DEFERRED
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  Constraints for Table PUBLICATION_EXTENSION
--------------------------------------------------------

  ALTER TABLE "GWAS"."PUBLICATION_EXTENSION" MODIFY ("PUBLICATION_ID" NOT NULL ENABLE);
  ALTER TABLE "GWAS"."PUBLICATION_EXTENSION" MODIFY ("ID" NOT NULL ENABLE);
