/*
################################################################################
Migration script to create PLATFORM and STUDY_PLATFORM tables and add columns
to the STUDY table

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
     "MANUFACTURER" VARCHAR2(255 CHAR));

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
   INSERT INTO PLATFORM (MANUFACTURER) VALUES('Affymetrix');
   INSERT INTO PLATFORM (MANUFACTURER) VALUES('Illumina');
   INSERT INTO PLATFORM (MANUFACTURER) VALUES('Perlegen');


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


--------------------------------------------------------
--  Update for Table STUDY
--------------------------------------------------------
    ALTER TABLE "STUDY"
        ADD ("POOLED" NUMBER(1,0),
        "SNP_COUNT" NUMBER(19,0),
        "QUALIFIER" VARCHAR2(255 CHAR),
        "IMPUTED" NUMBER(1,0),
        "STUDY_DESIGN_COMMENT" VARCHAR2(255 CHAR));
