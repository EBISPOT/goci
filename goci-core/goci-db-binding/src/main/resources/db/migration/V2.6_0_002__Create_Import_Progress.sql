/*
################################################################################
Creating Submission Import Progress table
author: Tudor Groza
date:    9 October 2020
version: 2.6.0.001
################################################################################
*/

--------------------------------------------------------
--  DDL for Table SUBMISSION_IMPORT_PROGRESS
--------------------------------------------------------

  CREATE TABLE "GWAS"."SUBMISSION_IMPORT_PROGRESS"
   (	"ID" NUMBER(*,0),
	"TIMESTAMP" DATE,
	"USER_EMAIL" VARCHAR2(4000 BYTE),
	"SUBMISSION_ID" VARCHAR2(4000 BYTE)
   ) SEGMENT CREATION DEFERRED
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  TABLESPACE "SPOT_DATA" ;

--------------------------------------------------------
--  DDL for Index SIP_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "GWAS"."SIP_PK" ON "GWAS"."SUBMISSION_IMPORT_PROGRESS" ("ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "SPOT_DATA" ;

--------------------------------------------------------
--  DDL for Index SIP_SID_IDX
--------------------------------------------------------

  CREATE INDEX "GWAS"."SIP_SID_IDX" ON "GWAS"."SUBMISSION_IMPORT_PROGRESS" ("SUBMISSION_ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS
  TABLESPACE "SPOT_DATA" ;

--------------------------------------------------------
--  Constraints for Table SUBMISSION_IMPORT_PROGRESS
--------------------------------------------------------

  ALTER TABLE "GWAS"."SUBMISSION_IMPORT_PROGRESS" MODIFY ("SUBMISSION_ID" NOT NULL ENABLE);
  ALTER TABLE "GWAS"."SUBMISSION_IMPORT_PROGRESS" MODIFY ("USER_EMAIL" NOT NULL ENABLE);
  ALTER TABLE "GWAS"."SUBMISSION_IMPORT_PROGRESS" ADD CONSTRAINT "SIP_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "SPOT_DATA"  ENABLE;
  ALTER TABLE "GWAS"."SUBMISSION_IMPORT_PROGRESS" MODIFY ("ID" NOT NULL ENABLE);