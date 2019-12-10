/*

################################################################################

Migration script to create STUDY_EXTENSION table

author: Jon Stewart
date:    12 Dec 2019
version: 2.3.0.007
################################################################################
*/
--------------------------------------------------------
--  DDL for Table STUDY_EXTENSION
--------------------------------------------------------

  CREATE TABLE "GWAS"."STUDY_EXTENSION"
   ( 	"ID" NUMBER(19,0),
   "STUDY_ID" NUMBER(19,0),
	"STATISTICAL_MODEL" VARCHAR2(255 BYTE),
	"BACKGROUND_TRAIT" VARCHAR2(255 BYTE),
	"MAPPED_BACKGROUND_TRAIT" VARCHAR2(255 BYTE),
	"COHORT" VARCHAR2(255 BYTE),
	"COHORT_SPECIFIC_REFERENCE" VARCHAR2(255 BYTE),
	"SUMMARY_STATISTICS_FILE" VARCHAR2(255 BYTE),
	"SUMMARY_STATISTICS_ASSEMBLY" VARCHAR2(255 BYTE)
   ) SEGMENT CREATION DEFERRED
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index STUDY_EXTENSION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "GWAS"."STUDY_EXTENSION_PK" ON "GWAS"."STUDY_EXTENSION" ("STUDY_ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  Constraints for Table STUDY_EXTENSION
--------------------------------------------------------

  ALTER TABLE "GWAS"."STUDY_EXTENSION" MODIFY ("STUDY_ID" NOT NULL ENABLE);
  ALTER TABLE "GWAS"."STUDY_EXTENSION" ADD CONSTRAINT "STUDY_EXTENSION_PK" PRIMARY KEY ("STUDY_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "USERS"  ENABLE;
