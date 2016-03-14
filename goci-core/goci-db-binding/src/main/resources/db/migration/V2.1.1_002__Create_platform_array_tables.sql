/*
################################################################################
Migration script to create PLATFORM, STUDY_PLATFORM and ARRAY_INFORMATION tableS

Designed for execution with Flyway database migrations tool; this should be
automatically run to completely generate the schema that is out-of-the-box
compatibile with the GOCI model (see
https://github.com/tburdett/goci/tree/2.x-dev/goci-core/goci-model for more).

author:  Dani Welter
date:    March 11th 2016
version: 2.1.1.002
################################################################################

#######################################
#  CREATE NEW TABLES AND CONSTRAINTS
#######################################
*/
--------------------------------------------------------
--  Create Table PLATFORM
--------------------------------------------------------

  CREATE TABLE "PLATFORM" (
     "ID" NUMBER(19,0),
     "PLATFORM" VARCHAR2(255 CHAR));

--------------------------------------------------------
--  DDL for Index PLATFORM_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "PLATFORM_ID_PK" ON "PLATFORM" ("ID");

--------------------------------------------------------
--  Constraints for Table PLATFORML
--------------------------------------------------------
  ALTER TABLE "PLATFORM" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "PLATFORM" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
-- Create trigger on PLATFORM
--------------------------------------------------------

CREATE OR REPLACE TRIGGER PLATFORM_TRG
BEFORE INSERT ON "PLATFORM"
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER PLATFORM_TRG ENABLE;

--------------------------------------------------------
-- Insert default values into PLATFORM
--------------------------------------------------------
   INSERT INTO PLATFORM (PLATFORM) VALUES('Affymetrix');
   INSERT INTO PLATFORM (PLATFORM) VALUES('Illumina');
   INSERT INTO PLATFORM (PLATFORM) VALUES('Perlegen');

--------------------------------------------------------
--  Create Table ARRAY_INFORMATION
--------------------------------------------------------

  CREATE TABLE "ARRAY_INFORMATION" (
     "ID" NUMBER(19,0),
     "SNP_COUNT" NUMBER(19,0),
     "QUALIFIER" VARCHAR2(255 CHAR),
     "IMPUTED" NUMBER(1,0),
     "POOLED" NUMBER(1,0),
     "COMMENT" VARCHAR2(255 CHAR),
     "STUDY_ID" NUMBER(19,0));

--------------------------------------------------------
--  DDL for Index ARRAY_INFORMATION_ID_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX "ARRAY_INFORMATION_ID_PK" ON "ARRAY_INFORMATION" ("ID");

--------------------------------------------------------
--  Constraints for Table ARRAY_INFORMATION
--------------------------------------------------------
  ALTER TABLE "ARRAY_INFORMATION" ADD PRIMARY KEY ("ID") ENABLE;
  ALTER TABLE "ARRAY_INFORMATION" MODIFY ("ID" NOT NULL ENABLE);

--------------------------------------------------------
-- Create trigger on ARRAY_INFORMATION
--------------------------------------------------------

CREATE OR REPLACE TRIGGER ARRAY_INFORMATION_TRG
BEFORE INSERT ON "ARRAY_INFORMATION"
FOR EACH ROW
    BEGIN
        IF :NEW.ID IS NULL THEN
            SELECT HIBERNATE_SEQUENCE.NEXTVAL INTO :NEW.ID FROM DUAL;
        END IF;
    END;
/
ALTER TRIGGER ARRAY_INFORMATION_TRG ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table ARRAY_INFORMATION
--------------------------------------------------------
  ALTER TABLE "ARRAY_INFORMATION" ADD CONSTRAINT "ARRAY_INFORMATION_STUDY_ID_FK" FOREIGN KEY ("STUDY_ID")
	  REFERENCES "STUDY" ("ID") ENABLE;

--------------------------------------------------------
--  DDL for Table STUDY_PLATFORM
--------------------------------------------------------
  CREATE TABLE "STUDY_PLATFORM" (
      "STUDY_ID" NUMBER(19,0),
      "PLATFORM_ID" NUMBER(19,0));


--------------------------------------------------------
--  Constraints for Table STUDY_PLATFORM
--------------------------------------------------------
  ALTER TABLE "STUDY_PLATFORM" MODIFY ("PLATFORM_ID" NOT NULL ENABLE);
  ALTER TABLE "STUDY_PLATFORM" MODIFY ("STUDY_ID" NOT NULL ENABLE);


--------------------------------------------------------
--  Ref Constraints for Table STUDY_PLATFORM
--------------------------------------------------------
  ALTER TABLE "STUDY_PLATFORM" ADD CONSTRAINT "STUDY_PLATFORM_ID_FK" FOREIGN KEY ("PLATFORM_ID")
	  REFERENCES "PLATFORM" ("ID") ENABLE;
  ALTER TABLE "STUDY_PLATFORM" ADD CONSTRAINT "STUDY_PLATFORM_STUDY_ID_FK" FOREIGN KEY ("STUDY_ID")
	  REFERENCES "STUDY" ("ID") ENABLE;


