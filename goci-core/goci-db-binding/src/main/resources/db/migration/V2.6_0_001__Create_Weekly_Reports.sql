/*

################################################################################

Creating Weekly Reports table

author: Tudor Groza
date:    9 October 2020
version: 2.6.0.001

################################################################################
*/

--------------------------------------------------------
--  DDL for Table WEEKLY_REPORT
--------------------------------------------------------

  CREATE TABLE "GWAS"."WEEKLY_REPORT"
   (	"ID" NUMBER(*,0),
	"TIMESTAMP" DATE,
	"WEEK_CODE" NUMBER(*,0),
	"WEEK_DATE" DATE,
	"TYPE" VARCHAR2(4000 BYTE),
	"STUDIES_CREATED" VARCHAR2(4000 BYTE),
	"STUDIES_LEVEL1" VARCHAR2(4000 BYTE),
	"STUDIES_LEVEL2" VARCHAR2(4000 BYTE),
	"STUDIES_PUBLISHED" VARCHAR2(4000 BYTE),
	"PUBS_CREATED" VARCHAR2(4000 BYTE),
	"PUBS_LEVEL1" VARCHAR2(4000 BYTE),
	"PUBS_LEVEL2" VARCHAR2(4000 BYTE),
	"PUBS_PUBLISHED" VARCHAR2(4000 BYTE)
   ) SEGMENT CREATION DEFERRED
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  TABLESPACE "SPOT_DATA" ;

--------------------------------------------------------
--  DDL for Index WEEKLY_REPORT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "GWAS"."WEEKLY_REPORT_PK" ON "GWAS"."WEEKLY_REPORT" ("ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "SPOT_DATA" ;

--------------------------------------------------------
--  DDL for Index REP_WEK_WD_IDX
--------------------------------------------------------

  CREATE INDEX "GWAS"."REP_WEK_WC_IDX" ON "GWAS"."WEEKLY_REPORT" ("WEEK_CODE")
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS
  TABLESPACE "SPOT_DATA" ;

--------------------------------------------------------
--  Constraints for Table WEEKLY_REPORT
--------------------------------------------------------

  ALTER TABLE "GWAS"."WEEKLY_REPORT" MODIFY ("WEEK_CODE" NOT NULL ENABLE);
  ALTER TABLE "GWAS"."WEEKLY_REPORT" ADD CONSTRAINT "WEEKLY_REPORT_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "SPOT_DATA"  ENABLE;
  ALTER TABLE "GWAS"."WEEKLY_REPORT" MODIFY ("ID" NOT NULL ENABLE);
