/*

################################################################################

Creating Studies Import table

author: Tudor Groza
date:    9 October 2020
version: 2.7.0.002

################################################################################
*/

--------------------------------------------------------
--  DDL for Table SUBMISSION_IMPORT_STUDY
--------------------------------------------------------

  CREATE TABLE "GWAS"."SUBMISSION_IMPORT_STUDY"
   (
   "ID" NUMBER(*,0),
   "TIMESTAMP" DATE,
   "CONTENT" CLOB,
   "SUCCESS" NUMBER(1,0),
   "FINALIZED" NUMBER(1,0),
   "ACCESSION_ID" VARCHAR2(4000 BYTE),
   "TAG" VARCHAR2(4000 BYTE),
   "SUBMISSION_ID" VARCHAR2(4000 BYTE)
   ) SEGMENT CREATION DEFERRED
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  TABLESPACE "SPOT_DATA" ;

--------------------------------------------------------
--  DDL for Index SUBMISSION_IMPORT_STUDY_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "GWAS"."SUBMISSION_IMPORT_STUDY_PK" ON "GWAS"."SUBMISSION_IMPORT_STUDY" ("ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "SPOT_DATA" ;

--------------------------------------------------------
--  DDL for Index SUBMISSION_ID_IDX
--------------------------------------------------------

  CREATE INDEX "GWAS"."SUBMISSION_ID_IDX" ON "GWAS"."SUBMISSION_IMPORT_STUDY" ("SUBMISSION_ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS
  TABLESPACE "SPOT_DATA" ;

--------------------------------------------------------
--  DDL for Index ACCESSION_ID_IDX
--------------------------------------------------------

  CREATE INDEX "GWAS"."ACCESSION_ID_IDX" ON "GWAS"."SUBMISSION_IMPORT_STUDY" ("ACCESSION_ID")
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS
  TABLESPACE "SPOT_DATA" ;

--------------------------------------------------------
--  Constraints for Table SUBMISSION_IMPORT_STUDY
--------------------------------------------------------

  ALTER TABLE "GWAS"."SUBMISSION_IMPORT_STUDY" MODIFY ("SUBMISSION_ID" NOT NULL ENABLE);
  ALTER TABLE "GWAS"."SUBMISSION_IMPORT_STUDY" ADD CONSTRAINT "SUBMISSION_IMPORT_STUDY_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255
  TABLESPACE "SPOT_DATA"  ENABLE;
  ALTER TABLE "GWAS"."SUBMISSION_IMPORT_STUDY" MODIFY ("ID" NOT NULL ENABLE);
