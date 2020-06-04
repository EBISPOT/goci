/*

################################################################################

Migration script to add unpublished study tables

author: Jon Stewart
date:    1 April 2020
version: 2.4.0.011

################################################################################
*/

--------------------------------------------------------
--  DDL for Table UNPUBLISHED_STUDY
--------------------------------------------------------

  CREATE TABLE "GWAS"."UNPUBLISHED_STUDY"
   (	"ID" NUMBER(*,0),
	"ACCESSION" VARCHAR2(20 BYTE),
	"STUDY_TAG" VARCHAR2(20 BYTE),
	"SUMMARY_STATS_FILE" VARCHAR2(255 BYTE),
	"CHECKSUM" VARCHAR2(20 BYTE),
	"TRAIT" VARCHAR2(255 BYTE),
	"BACKGROUND_TRAIT" VARCHAR2(255 BYTE)
   ) SEGMENT CREATION DEFERRED
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  TABLESPACE "SPOT_DATA" ;
--------------------------------------------------------
--  DDL for Index UNPUBLISHED_STUDY_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "GWAS"."UNPUBLISHED_STUDY_PK" ON "GWAS"."UNPUBLISHED_STUDY" ("ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "SPOT_DATA" ;
--------------------------------------------------------
--  DDL for Index US_GCST_PK
--------------------------------------------------------

  CREATE INDEX "GWAS"."US_GCST_PK" ON "GWAS"."UNPUBLISHED_STUDY" ("ACCESSION")
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS
  TABLESPACE "SPOT_DATA" ;
--------------------------------------------------------
--  Constraints for Table UNPUBLISHED_STUDY
--------------------------------------------------------

  ALTER TABLE "GWAS"."UNPUBLISHED_STUDY" MODIFY ("STUDY_TAG" NOT NULL ENABLE);
  ALTER TABLE "GWAS"."UNPUBLISHED_STUDY" MODIFY ("ACCESSION" NOT NULL ENABLE);
  ALTER TABLE "GWAS"."UNPUBLISHED_STUDY" ADD CONSTRAINT "UNPUBLISHED_STUDY_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "SPOT_DATA"  ENABLE;
  ALTER TABLE "GWAS"."UNPUBLISHED_STUDY" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
--  DDL for Table UNPUBLISHED_STUDY_TO_WORK
--------------------------------------------------------

  CREATE TABLE "GWAS"."UNPUBLISHED_STUDY_TO_WORK"
   (	"STUDY_ID" NUMBER(*,0),
	"WORK_ID" NUMBER(*,0)
   ) SEGMENT CREATION DEFERRED
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  TABLESPACE "SPOT_DATA" ;
--------------------------------------------------------
--  DDL for Index UP_STW_SID_PK
--------------------------------------------------------

  CREATE INDEX "GWAS"."UP_STW_SID_PK" ON "GWAS"."UNPUBLISHED_STUDY_TO_WORK" ("STUDY_ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS
  TABLESPACE "SPOT_DATA" ;
--------------------------------------------------------
--  Constraints for Table UNPUBLISHED_STUDY_TO_WORK
--------------------------------------------------------

  ALTER TABLE "GWAS"."UNPUBLISHED_STUDY_TO_WORK" MODIFY ("WORK_ID" NOT NULL ENABLE);
  ALTER TABLE "GWAS"."UNPUBLISHED_STUDY_TO_WORK" MODIFY ("STUDY_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  DDL for Table BODY_OF_WORK
--------------------------------------------------------

  CREATE TABLE "GWAS"."BODY_OF_WORK"
   (	"ID" NUMBER(*,0),
	"PUB_ID" VARCHAR2(50 BYTE),
	"PUB_MED_ID" VARCHAR2(50 BYTE),
	"JOURNAL" VARCHAR2(50 BYTE),
	"TITLE" VARCHAR2(255 BYTE),
	"FIRST_AUTHOR" VARCHAR2(50 BYTE),
	"PUB_DATE" DATE
   ) SEGMENT CREATION DEFERRED
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  TABLESPACE "SPOT_DATA" ;
--------------------------------------------------------
--  DDL for Index BODY_OF_WORK_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "GWAS"."BODY_OF_WORK_PK" ON "GWAS"."BODY_OF_WORK" ("ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "SPOT_DATA" ;
--------------------------------------------------------
--  DDL for Index BOW_PUB_ID_IDX
--------------------------------------------------------

  CREATE INDEX "GWAS"."BOW_PUB_ID_IDX" ON "GWAS"."BODY_OF_WORK" ("PUB_ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS
  TABLESPACE "SPOT_DATA" ;
--------------------------------------------------------
--  Constraints for Table BODY_OF_WORK
--------------------------------------------------------

  ALTER TABLE "GWAS"."BODY_OF_WORK" ADD CONSTRAINT "BODY_OF_WORK_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "SPOT_DATA"  ENABLE;
  ALTER TABLE "GWAS"."BODY_OF_WORK" MODIFY ("PUB_ID" NOT NULL ENABLE);
  ALTER TABLE "GWAS"."BODY_OF_WORK" MODIFY ("ID" NOT NULL ENABLE);
